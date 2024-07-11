package com.example.Assignment_0.dto;

import com.example.Assignment_0.entity.Employee;
import com.example.Assignment_0.entity.Project;
import com.example.Assignment_0.utils.ProjectRole;

import lombok.Data;

import java.util.List;

@Data
public class ProjectAllocationResponse {
    private Long id;

    private Long employeeId;

    private Long projectId;

    private ProjectRole projectRole;

    private Double allocationPercentage;

    private List<String> techStack;

    private Double durationWeeks;
}
