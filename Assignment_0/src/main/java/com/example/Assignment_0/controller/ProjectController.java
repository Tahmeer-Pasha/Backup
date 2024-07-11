package com.example.Assignment_0.controller;

import com.example.Assignment_0.dto.*;
import com.example.Assignment_0.entity.Employee;
import com.example.Assignment_0.entity.Project;
import com.example.Assignment_0.entity.ProjectAllocation;
import com.example.Assignment_0.service.EmployeeServiceImpl;
import com.example.Assignment_0.service.ProjectAllocationServiceImpl;
import com.example.Assignment_0.service.ProjectServiceImpl;
import com.example.Assignment_0.utils.Status;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/project")
@RequiredArgsConstructor
public class ProjectController {
    private static final Logger log = LoggerFactory.getLogger(ProjectController.class);
    private final EmployeeServiceImpl employeeService;
    private final ProjectServiceImpl projectService;
    private final ProjectAllocationServiceImpl projectAllocationService;

    @GetMapping
    private ResponseEntity<ApiResponse<?>> getAllProject(){
        try{
            List<Project> projectsList = projectService.getAllProjects();
            List<ProjectResponse> projectResponsesList = projectsList.stream().map(this::getProjectResponse).toList();
            return ResponseEntity.ok(new ApiResponse<>("Retrieved all the projects",200, projectResponsesList));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(e.getMessage(), 500, null));
        }
    }

    @PostMapping("/create")
    private ResponseEntity<ApiResponse<?>> createProject(HttpServletRequest request, @RequestBody ProjectRequest projectRequest) {
        try {
            UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");
            if (userDetails == null || userDetails.getAuthorities().stream().noneMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>("Unauthorized! Project can be created only by ADMIN.", 401, null));
            }
            // Retrieve account manager and project manager by their IDs
            Employee accountManager = employeeService.findEmployeeById(projectRequest.getAccountManagerId());
            Employee projectManager = employeeService.findEmployeeById(projectRequest.getProjectManagerId());

            // Create a new Project entity
            Project project = getProject(projectRequest, accountManager, projectManager);

            // Save the project to the database
            Project createdProject = projectService.saveProject(project);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>("Project created successfully", 201, getProjectResponse(createdProject)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(e.getMessage(), 500, null));
        }
    }

    @GetMapping("/{id}")
    private ResponseEntity<ApiResponse<?>> getProjectDetailsById(@PathVariable Long id, HttpServletRequest request){
        try{
            UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");
            if (userDetails == null || userDetails.getAuthorities().stream().noneMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>("Unauthorized! Project details can be viewed only by ADMIN.", 401, null));
            }
            Project project = projectService.getProjectById(id);

            if(project == null)
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>("Project with given id not found",404, null));

            List<ProjectAllocation> projectAllocation = projectAllocationService.getProjectAllocationByProjectId(project.getId());

            List<EmployeeAllocatedToProjectDetails> projectAllocationDetailsList = projectAllocation.stream().map(
                    allocation -> {
                EmployeeAllocatedToProjectDetails employeeAllocatedToProjectDetails = new EmployeeAllocatedToProjectDetails();
                employeeAllocatedToProjectDetails.setEmpId(allocation.getEmployee().getId());
                employeeAllocatedToProjectDetails.setEmpName(allocation.getEmployee().getName());
                employeeAllocatedToProjectDetails.setAllocationPercentage(allocation.getAllocationPercentage());
                employeeAllocatedToProjectDetails.setRole(allocation.getProjectRole());
                employeeAllocatedToProjectDetails.setTechStack(allocation.getTechStack());
                return employeeAllocatedToProjectDetails;
            }).toList();

            CompleteProjectResponse projectResponse = getCompleteProjectResponse(project, projectAllocationDetailsList);

            return ResponseEntity.ok(new ApiResponse<>("Retrieved project with allocated resources Successfully",200, projectResponse));
        }catch (Exception e){
            return ResponseEntity.internalServerError().body(new ApiResponse<>(e.getMessage(),500,null));
        }
    }

    @PatchMapping("/status/{projectId}")
    private ResponseEntity<ApiResponse<?>> updateProjectStatus(HttpServletRequest request, @RequestParam Status status, @PathVariable Long projectId){
        try{
            UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");
            if (userDetails == null || userDetails.getAuthorities().stream().noneMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>("Unauthorized! Project details can be viewed only by ADMIN.", 401, null));
            }
            ProjectDto project = projectService.updateStatus(projectId,status);
            if(project == null)throw new RuntimeException("Project Not Found! And hence status not updated");
            return ResponseEntity.ok(new ApiResponse<>("Status updated successfully!",200,project));
        }catch (Exception e){
            return ResponseEntity.internalServerError().body(new ApiResponse<>(e.getMessage(),500,null));
        }
    }

    private static CompleteProjectResponse getCompleteProjectResponse(Project project, List<EmployeeAllocatedToProjectDetails> projectAllocationDetailsList) {
        CompleteProjectResponse projectResponse = new CompleteProjectResponse();
        projectResponse.setProjectType(project.getProjectType());
        projectResponse.setProjectManagerName(project.getProjectManager().getName());
        projectResponse.setStatus(project.getStatus());
        projectResponse.setDescription(project.getDescription());
        projectResponse.setSourceClient(project.getSourceClient());
        projectResponse.setEndClient(project.getEndClient());
        projectResponse.setAllocatedEmployees(projectAllocationDetailsList);
        return projectResponse;
    }

    private ProjectResponse getProjectResponse(Project createdProject) {
        ProjectResponse projectResponse = new ProjectResponse();
        projectResponse.setId(createdProject.getId());
        projectResponse.setProjectName(createdProject.getProjectName());
        projectResponse.setProjectType(createdProject.getProjectType());
        projectResponse.setSourceClient(createdProject.getSourceClient());
        projectResponse.setEndClient(createdProject.getEndClient());
        projectResponse.setDescription(createdProject.getDescription());
        projectResponse.setProjectManagerId(createdProject.getProjectManager().getId());
        projectResponse.setProjectManagerName(createdProject.getProjectManager().getName());
        projectResponse.setAccountManagerId(createdProject.getAccountManager().getId());
        projectResponse.setAccountManagerName(createdProject.getAccountManager().getName());
        projectResponse.setStatus(createdProject.getStatus());
        return projectResponse;
    }

    private static Project getProject(ProjectRequest projectRequest, Employee accountManager, Employee projectManager) {
        Project project = new Project();
        project.setProjectName(projectRequest.getProjectName());
        project.setProjectType(projectRequest.getProjectType());
        project.setSourceClient(projectRequest.getSourceClient());
        project.setEndClient(projectRequest.getEndClient());
        project.setDescription(projectRequest.getDescription());
        project.setAccountManager(accountManager);
        project.setProjectManager(projectManager);
        project.setStatus(projectRequest.getStatus());
        project.setAllocations(null);
        return project;
    }
}
