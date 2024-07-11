package com.example.Assignment_0.controller;

import com.example.Assignment_0.dto.*;
import com.example.Assignment_0.entity.Employee;
import com.example.Assignment_0.entity.Project;
import com.example.Assignment_0.entity.ProjectAllocation;
import com.example.Assignment_0.service.EmployeeServiceImpl;
import com.example.Assignment_0.service.ProjectAllocationServiceImpl;
import com.example.Assignment_0.service.ProjectServiceImpl;
import com.example.Assignment_0.utils.enums.Status;
import com.example.Assignment_0.utils.AuthorizationUtils;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/project")
@RequiredArgsConstructor
@SecurityRequirement(name = "JWT Authentication")
public class ProjectController {

    private static final Logger log = LoggerFactory.getLogger(ProjectController.class);

    private final EmployeeServiceImpl employeeService;
    private final ProjectServiceImpl projectService;
    private final ProjectAllocationServiceImpl projectAllocationService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProjectResponse>>> getAllProjects(HttpServletRequest request) {
        try {
            if (!AuthorizationUtils.isAuthorized(request)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>("Unauthorized! Project details can be viewed only by ADMIN.", 401, null));
            }

            List<Project> projectsList = projectService.getAllProjects();
            List<ProjectResponse> projectResponsesList = projectsList.stream().map(this::mapToProjectResponse).collect(Collectors.toList());
            return ResponseEntity.ok(new ApiResponse<>("Retrieved all the projects", 200, projectResponsesList));
        } catch (Exception e) {
            log.error("Error retrieving all projects: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(e.getMessage(), 500, null));
        }
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<ProjectResponse>> createProject(HttpServletRequest request, @RequestBody ProjectRequest projectRequest) {
        try {
            if (!AuthorizationUtils.isAuthorized(request)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>("Unauthorized! Project can be created only by ADMIN.", 401, null));
            }

            Employee accountManager = employeeService.findEmployeeById(projectRequest.getAccountManagerId());
            Employee projectManager = employeeService.findEmployeeById(projectRequest.getProjectManagerId());

            Project project = buildProject(projectRequest, accountManager, projectManager);

            Project createdProject = projectService.saveProject(project);

            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>("Project created successfully", 201, mapToProjectResponse(createdProject)));
        } catch (Exception e) {
            log.error("Error creating project: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(e.getMessage(), 500, null));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CompleteProjectResponse>> getProjectDetailsById(@PathVariable Long id, HttpServletRequest request) {
        try {
            if (!AuthorizationUtils.isAuthorized(request)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>("Unauthorized! Project details can be viewed only by ADMIN.", 401, null));
            }

            Project project = projectService.getProjectById(id);

            if (project == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>("Project with given id not found", 404, null));
            }

            List<ProjectAllocation> projectAllocation = projectAllocationService.getProjectAllocationsByProjectId(project.getId());

            List<EmployeeAllocatedToProjectDetails> projectAllocationDetailsList = projectAllocation.stream()
                    .map(this::mapToEmployeeAllocatedToProjectDetails)
                    .collect(Collectors.toList());

            CompleteProjectResponse projectResponse = buildCompleteProjectResponse(project, projectAllocationDetailsList);

            return ResponseEntity.ok(new ApiResponse<>("Retrieved project with allocated resources Successfully", 200, projectResponse));
        } catch (Exception e) {
            log.error("Error retrieving project details: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(e.getMessage(), 500, null));
        }
    }

    @PatchMapping("/status/{projectId}")
    public ResponseEntity<ApiResponse<ProjectDto>> updateProjectStatus(HttpServletRequest request, @RequestParam Status status, @PathVariable Long projectId) {
        try {
            if (!AuthorizationUtils.isAuthorized(request)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>("Unauthorized! Project status can be updated only by ADMIN.", 401, null));
            }

            ProjectDto project = projectService.updateStatus(projectId, status);

            if (project == null) {
                throw new RuntimeException("Project Not Found! Status not updated.");
            }

            return ResponseEntity.ok(new ApiResponse<>("Status updated successfully!", 200, project));
        } catch (RuntimeException e) {
            log.error("Error updating project status: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(e.getMessage(), 500, null));
        }
    }

    private Project buildProject(ProjectRequest projectRequest, Employee accountManager, Employee projectManager) {
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

    private ProjectResponse mapToProjectResponse(Project project) {
        ProjectResponse response = new ProjectResponse();
        response.setId(project.getId());
        response.setProjectName(project.getProjectName());
        response.setProjectType(project.getProjectType());
        response.setSourceClient(project.getSourceClient());
        response.setEndClient(project.getEndClient());
        response.setDescription(project.getDescription());
        response.setProjectManagerId(project.getProjectManager().getId());
        response.setProjectManagerName(project.getProjectManager().getName());
        response.setAccountManagerId(project.getAccountManager().getId());
        response.setAccountManagerName(project.getAccountManager().getName());
        response.setStatus(project.getStatus());
        return response;
    }

    private CompleteProjectResponse buildCompleteProjectResponse(Project project, List<EmployeeAllocatedToProjectDetails> projectAllocationDetailsList) {
        CompleteProjectResponse response = new CompleteProjectResponse();
        response.setProjectType(project.getProjectType());
        response.setProjectManagerName(project.getProjectManager().getName());
        response.setStatus(project.getStatus());
        response.setDescription(project.getDescription());
        response.setSourceClient(project.getSourceClient());
        response.setEndClient(project.getEndClient());
        response.setAllocatedEmployees(projectAllocationDetailsList);
        return response;
    }

    private EmployeeAllocatedToProjectDetails mapToEmployeeAllocatedToProjectDetails(ProjectAllocation allocation) {
        EmployeeAllocatedToProjectDetails details = new EmployeeAllocatedToProjectDetails();
        details.setEmpId(allocation.getEmployee().getId());
        details.setEmpName(allocation.getEmployee().getName());
        details.setAllocationPercentage(allocation.getAllocationPercentage());
        details.setRole(allocation.getProjectRole());
        details.setTechStack(allocation.getTechStack());
        return details;
    }
}
