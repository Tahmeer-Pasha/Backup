package com.example.Assignment_0.dto;

import com.example.Assignment_0.utils.enums.ProjectRole;

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
