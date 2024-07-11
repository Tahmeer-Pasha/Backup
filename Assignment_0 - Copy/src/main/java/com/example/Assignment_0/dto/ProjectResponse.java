package com.example.Assignment_0.dto;

import com.example.Assignment_0.utils.enums.ProjectType;
import com.example.Assignment_0.utils.enums.Status;
import lombok.Data;

@Data
public class ProjectResponse {
    private Long id;
    private String projectName;
    private ProjectType projectType;
    private String sourceClient;
    private String endClient;
    private String description;
    private Long accountManagerId;
    private String accountManagerName;
    private Long projectManagerId;
    private String projectManagerName;
    private Status status;
}
