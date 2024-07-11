package com.example.Assignment_0.dto;

import com.example.Assignment_0.utils.enums.ProjectRole;
import lombok.Data;

import java.util.List;

@Data
public class ProjectAllocationRequest {
    private Long empId;
    private Long projectId;
    private ProjectRole role;
    private Double allocationPercentage;
    private List<String> techStack;
    private Double duration;
}
