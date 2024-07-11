package com.example.Assignment_0.dto;

import com.example.Assignment_0.utils.ProjectType;
import com.example.Assignment_0.utils.Status;

import lombok.Data;

import java.util.List;

@Data
public class ProjectDto {
    private Long id;

    private ProjectType projectType;

    private String sourceClient;

    private String endClient;

    private String description;

    private Long  accountManagerId;

    private String  accountManagerName;

    private Long projectManagerId;

    private String projectManagerName;

    private Status status;

    private List<ProjectAllocationDto> allocations;
}
