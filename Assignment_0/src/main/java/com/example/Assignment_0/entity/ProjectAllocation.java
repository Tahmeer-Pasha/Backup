package com.example.Assignment_0.entity;

import com.example.Assignment_0.utils.ProjectRole;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "project_allocation")
public class ProjectAllocation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @Column(name = "project_role")
    private ProjectRole projectRole;

    @Column(name = "allocation_percentage")
    private Double allocationPercentage;

    @Column(name = "tech_stack")
    private List<String> techStack;

    @Column(name = "duration_weeks")
    private Double durationWeeks;
}

