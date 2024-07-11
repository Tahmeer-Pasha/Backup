package com.example.Assignment_0.dto;

import com.example.Assignment_0.utils.ProjectRole;
import com.example.Assignment_0.utils.Role;
import lombok.Data;

import java.util.List;

@Data
public class EmployeeAllocatedToProjectDetails{
    private Long empId;
    private String empName;
    private Double allocationPercentage;
    private ProjectRole role;
    private List<String> techStack;
}
