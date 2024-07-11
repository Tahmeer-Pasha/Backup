package com.example.Assignment_0.service.interfaces;

import com.example.Assignment_0.dto.ProjectAllocationDto;
import com.example.Assignment_0.dto.ProjectAllocationRequest;
import com.example.Assignment_0.dto.ProjectAllocationResponse;
import com.example.Assignment_0.entity.ProjectAllocation;

import java.util.List;

public interface ProjectAllocationService {
    ProjectAllocationResponse assignProject(ProjectAllocationRequest projectAllocationRequest);

    List<ProjectAllocation> getAllProjectAllocations();


    List<ProjectAllocation> getProjectAllocationsByProjectId(Long id);

    void removeFromProject(ProjectAllocationRequest projectAllocationRequest);

    List<ProjectAllocationDto> getProjectAllocationDtoListByProjectId(Long id);
}
