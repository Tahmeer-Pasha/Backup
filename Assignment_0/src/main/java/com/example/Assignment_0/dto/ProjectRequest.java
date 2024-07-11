package com.example.Assignment_0.dto;

import com.example.Assignment_0.utils.ProjectType;
import com.example.Assignment_0.utils.Status;
import lombok.Data;

@Data
public class ProjectRequest {
    private String projectName;
    private ProjectType projectType;
    private String sourceClient;
    private String endClient;
    private String description;
    private Long accountManagerId;
    private Long projectManagerId;
    private Status status;
}
