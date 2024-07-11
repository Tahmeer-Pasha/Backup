package com.example.Assignment_0.dto;

import lombok.Data;

import java.util.List;

@Data
public class EmployeeAllocationDto {
    private Long employeeId;
    private String employeeName;
    private List<ProjectAllocationDto> projectAllocations;
}
