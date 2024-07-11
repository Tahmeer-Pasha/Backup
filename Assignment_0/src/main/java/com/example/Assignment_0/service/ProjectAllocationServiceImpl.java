package com.example.Assignment_0.service;

import com.example.Assignment_0.dto.ProjectAllocationDto;
import com.example.Assignment_0.dto.ProjectAllocationRequest;
import com.example.Assignment_0.dto.ProjectAllocationResponse;
import com.example.Assignment_0.entity.Employee;
import com.example.Assignment_0.entity.Project;
import com.example.Assignment_0.entity.ProjectAllocation;
import com.example.Assignment_0.repository.ProjectAllocationRepository;
import com.example.Assignment_0.repository.ProjectRepository;
import com.example.Assignment_0.service.interfaces.ProjectAllocationService;
import com.example.Assignment_0.utils.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectAllocationServiceImpl implements ProjectAllocationService {
    private final ProjectAllocationRepository projectAllocationRepository;
    private final ProjectRepository projectRepository;
    private final EmployeeServiceImpl employeeService;
    private final ProjectServiceImpl projectService;

    @Override
    public ProjectAllocationResponse assignProject(ProjectAllocationRequest projectAllocationRequest) {
        Employee employee = employeeService.findEmployeeById(projectAllocationRequest.getEmpId());
        Project project = projectService.getProjectById(projectAllocationRequest.getProjectId());
        if(project.getStatus().equals(Status.INACTIVE))throw new RuntimeException("Cannot allocate resources to an INACTIVE project!!");
        ProjectAllocation allocation = projectAllocationRepository.findByProjectIdAndEmployeeId(projectAllocationRequest.getProjectId(), projectAllocationRequest.getEmpId());
        if (allocation != null)throw new RuntimeException("Employee is already assigned to the project");
        ProjectAllocation projectAllocation = getProjectAllocation(projectAllocationRequest, employee, project);
        ProjectAllocation allocatedProject =  projectAllocationRepository.save(projectAllocation);
        project.getAllocations().add(allocatedProject);
        projectRepository.save(project);
        return getProjectAllocationResponse(allocatedProject);
    }

    private static ProjectAllocation getProjectAllocation(ProjectAllocationRequest projectAllocationRequest, Employee employee, Project project) {
        ProjectAllocation projectAllocation = new ProjectAllocation();
        projectAllocation.setEmployee(employee);
        projectAllocation.setProject(project);
        projectAllocation.setProjectRole(projectAllocationRequest.getRole());
        projectAllocation.setAllocationPercentage(projectAllocationRequest.getAllocationPercentage());
        projectAllocation.setTechStack(projectAllocationRequest.getTechStack());
        projectAllocation.setDurationWeeks(projectAllocationRequest.getDuration());
        return projectAllocation;
    }

    private static ProjectAllocationResponse getProjectAllocationResponse(ProjectAllocation allocatedProject) {
        ProjectAllocationResponse response = new ProjectAllocationResponse();
        response.setId(allocatedProject.getId());
        response.setProjectId(allocatedProject.getProject().getId());
        response.setProjectRole(allocatedProject.getProjectRole());
        response.setAllocationPercentage(allocatedProject.getAllocationPercentage());
        response.setDurationWeeks(allocatedProject.getDurationWeeks());
        response.setEmployeeId(allocatedProject.getEmployee().getId());
        response.setTechStack(allocatedProject.getTechStack());
        return response;
    }

    @Override
    public List<ProjectAllocation> getAllProjectAllocations() {
        return projectAllocationRepository.findAll();
    }

    @Override
    public List<ProjectAllocation> getProjectAllocationById(Long projectId) {
      return  projectAllocationRepository.findByProjectId(projectId);

    }

    @Override
    public List<ProjectAllocation> getProjectAllocationByProjectId(Long id) {
        return projectAllocationRepository.findByProjectId(id);
    }

    @Override
    public List<ProjectAllocationDto> getProjectAllocationListByProjectId(Long id) {
        List<ProjectAllocation> projectAllocations = projectAllocationRepository.findByProjectId(id);
        return projectAllocations.stream().map(employeeService::convertToProjectAllocationDto).toList();
    }

    @Override
    public void removeFromProject(ProjectAllocationRequest projectAllocationRequest) {
        ProjectAllocation projectAllocation = projectAllocationRepository.findByProjectIdAndEmployeeId(projectAllocationRequest.getProjectId(), projectAllocationRequest.getEmpId());
        projectAllocationRepository.delete(projectAllocation);
    }
}
