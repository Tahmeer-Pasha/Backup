package com.example.Assignment_0.repository;

import com.example.Assignment_0.entity.Employee;
import com.example.Assignment_0.entity.ProjectAllocation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectAllocationRepository extends JpaRepository<ProjectAllocation,Long> {
    List<ProjectAllocation> findByProjectId(Long id);

    ProjectAllocation findByProjectIdAndEmployeeId(Long projectId, Long empId);

    Optional<Object> findByEmployee(Employee employee);

    List<ProjectAllocation> findByEmployeeId(Long employeeId);
}
