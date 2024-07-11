package com.example.Assignment_0.entity;

import com.example.Assignment_0.utils.Role;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

import java.util.List;

@Entity
@Data
@Table(name = "employee")
@NoArgsConstructor
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    @NonNull
    private String name;

    @Column(name = "email")
    private String email;

    @NonNull
    @Column(name = "password")
    private String password;

    @Column(name = "tech_stack")
    private List<String> techStack;

    @Column(name = "years_of_experience")
    private Integer yearsOfExperience;

    @Column(name = "years_in_webknot")
    private Double yearsInWebknot;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role;


    @OneToMany(mappedBy = "accountManager", fetch = FetchType.LAZY)
    private List<Project> managedAccounts;

    @OneToMany(mappedBy = "projectManager", fetch = FetchType.LAZY)
    private List<Project> managedProjects;

    @OneToMany(mappedBy = "employee", fetch = FetchType.LAZY)
    private List<ProjectAllocation> projectAllocations;
}