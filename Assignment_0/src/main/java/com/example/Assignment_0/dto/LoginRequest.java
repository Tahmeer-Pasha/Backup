package com.example.Assignment_0.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}
