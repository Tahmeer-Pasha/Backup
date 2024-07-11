package com.example.Assignment_0.dto;

import com.example.Assignment_0.utils.enums.ProjectType;
import com.example.Assignment_0.utils.enums.Status;
import lombok.Data;

import java.util.List;

@Data
public class CompleteProjectResponse {
    private ProjectType projectType;
    private String sourceClient;
    private String endClient;
    private String description;
    private String accountManagerName;
    private String projectManagerName;
    private Status status;
    private List<EmployeeAllocatedToProjectDetails> allocatedEmployees;
}

