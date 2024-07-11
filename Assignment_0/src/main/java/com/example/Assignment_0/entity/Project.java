package com.example.Assignment_0.entity;

import com.example.Assignment_0.utils.ProjectType;
import com.example.Assignment_0.utils.Status;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "Project")
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "project_name")
    private String projectName;

    @Column(name = "project_type")
    private ProjectType projectType;

    @Column(name = "source_client")
    private String sourceClient;

    @Column(name = "end_client")
    private String endClient;

    @Column(name = "description")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_manager_id")
    private Employee accountManager;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_manager_id")
    private Employee projectManager;

    @Column(name = "status")
    private Status status;

    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY)
    private List<ProjectAllocation> allocations;
}