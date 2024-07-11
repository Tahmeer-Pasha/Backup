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
import com.example.Assignment_0.utils.enums.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectAllocationServiceImpl implements ProjectAllocationService {

    private final ProjectAllocationRepository projectAllocationRepository;
    private final ProjectRepository projectRepository;
    private final EmployeeServiceImpl employeeService;
    private final ProjectServiceImpl projectService;

    @Override
    @Transactional
    public ProjectAllocationResponse assignProject(ProjectAllocationRequest projectAllocationRequest) {
        Employee employee = employeeService.findEmployeeById(projectAllocationRequest.getEmpId());
        Project project = projectService.getProjectById(projectAllocationRequest.getProjectId());

        if (project.getStatus() == Status.INACTIVE) {
            throw new RuntimeException("Cannot allocate resources to an INACTIVE project!!");
        }

        ProjectAllocation existingAllocation = projectAllocationRepository.findByProjectIdAndEmployeeId(project.getId(), employee.getId());
        if (existingAllocation != null) {
            throw new RuntimeException("Employee is already assigned to the project");
        }

        ProjectAllocation projectAllocation = createProjectAllocation(projectAllocationRequest, employee, project);
        ProjectAllocation savedAllocation = projectAllocationRepository.save(projectAllocation);

        project.getAllocations().add(savedAllocation);
        projectRepository.save(project);

        return mapToProjectAllocationResponse(savedAllocation);
    }

    private ProjectAllocation createProjectAllocation(ProjectAllocationRequest request, Employee employee, Project project) {
        ProjectAllocation allocation = new ProjectAllocation();
        allocation.setEmployee(employee);
        allocation.setProject(project);
        allocation.setProjectRole(request.getRole());
        allocation.setAllocationPercentage(request.getAllocationPercentage());
        allocation.setTechStack(request.getTechStack());
        allocation.setDurationWeeks(request.getDuration());
        return allocation;
    }

    private ProjectAllocationResponse mapToProjectAllocationResponse(ProjectAllocation allocation) {
        ProjectAllocationResponse response = new ProjectAllocationResponse();
        response.setId(allocation.getId());
        response.setProjectId(allocation.getProject().getId());
        response.setProjectRole(allocation.getProjectRole());
        response.setAllocationPercentage(allocation.getAllocationPercentage());
        response.setDurationWeeks(allocation.getDurationWeeks());
        response.setEmployeeId(allocation.getEmployee().getId());
        response.setTechStack(allocation.getTechStack());
        return response;
    }

    @Override
    public List<ProjectAllocation> getAllProjectAllocations() {
        return projectAllocationRepository.findAll();
    }

    @Override
    public List<ProjectAllocation> getProjectAllocationsByProjectId(Long projectId) {
        return projectAllocationRepository.findByProjectId(projectId);
    }

    @Override
    public List<ProjectAllocationDto> getProjectAllocationDtoListByProjectId(Long projectId) {
        List<ProjectAllocation> projectAllocations = projectAllocationRepository.findByProjectId(projectId);
        return projectAllocations.stream()
                .map(employeeService::convertToProjectAllocationDto)
                .toList();
    }

    @Override
    public void removeFromProject(ProjectAllocationRequest request) {
        ProjectAllocation allocation = projectAllocationRepository.findByProjectIdAndEmployeeId(request.getProjectId(), request.getEmpId());
        if (allocation != null) {
            projectAllocationRepository.delete(allocation);
        }
    }
}
