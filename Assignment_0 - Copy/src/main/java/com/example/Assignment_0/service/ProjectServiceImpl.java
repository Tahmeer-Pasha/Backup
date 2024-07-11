package com.example.Assignment_0.service;

import com.example.Assignment_0.dto.ProjectDto;
import com.example.Assignment_0.entity.Project;
import com.example.Assignment_0.entity.ProjectAllocation;
import com.example.Assignment_0.repository.ProjectAllocationRepository;
import com.example.Assignment_0.repository.ProjectRepository;
import com.example.Assignment_0.service.interfaces.ProjectService;
import com.example.Assignment_0.utils.enums.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {


    private final ProjectRepository projectRepository;
    private final ProjectAllocationRepository projectAllocationRepository;
    private final EmployeeServiceImpl employeeService;

    @Override
    public Project saveProject(Project project) {
        return projectRepository.save(project);
    }

    @Override
    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    @Override
    public Project getProjectById(Long id) {
        Optional<Project> optionalProject = projectRepository.findById(id);
        return optionalProject.orElse(null);
    }

    @Override
    @Transactional
    public ProjectDto updateStatus(Long projectId, Status status) {
        Optional<Project> optionalProject = projectRepository.findById(projectId);
        if (optionalProject.isEmpty()) {
            return null;
        }

        Project project = optionalProject.get();
        project.setStatus(status);

        if (status == Status.INACTIVE) {
            List<ProjectAllocation> allocationList = projectAllocationRepository.findByProjectId(projectId);
            projectAllocationRepository.deleteAllInBatch(allocationList);
        }

        Project savedProject = projectRepository.save(project);
        return employeeService.createProjectDto(savedProject);
    }
}
