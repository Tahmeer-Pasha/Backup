package com.example.Assignment_0.dto;

import com.example.Assignment_0.utils.Role;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class RegistrationResponse {
    private Long id;
    private String name;
    private String email;
    private List<String> techStack;
    private Integer yearsOfExperience;
    private Double yearsInWebknot;
    private Role role;
}

