package com.example.Assignment_0.dto;

import com.example.Assignment_0.utils.ProjectType;
import com.example.Assignment_0.utils.Status;
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

