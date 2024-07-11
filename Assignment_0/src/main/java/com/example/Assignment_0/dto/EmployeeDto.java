package com.example.Assignment_0.dto;

import com.example.Assignment_0.utils.Role;
import lombok.Data;

import java.util.List;

@Data
public class EmployeeDto {
    private Long id;
    private String employeeName;
    private String employeeEmail;
    private List<String> techStack;
    private Double totalYearsOfExperience;
    private Double yearsInWebknot;
    private Role role;
    private List<ProjectDto> managedAccounts;
    private List<ProjectDto> managedProjects;
}
