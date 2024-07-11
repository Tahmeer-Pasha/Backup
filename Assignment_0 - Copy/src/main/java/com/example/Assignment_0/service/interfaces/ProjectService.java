package com.example.Assignment_0.service.interfaces;

import com.example.Assignment_0.dto.ProjectDto;
import com.example.Assignment_0.entity.Project;
import com.example.Assignment_0.utils.enums.Status;

import java.util.List;

public interface ProjectService {
    Project saveProject(Project project);
    List<Project> getAllProjects();

    Project getProjectById(Long id);

    ProjectDto updateStatus(Long projectId, Status status);
}
