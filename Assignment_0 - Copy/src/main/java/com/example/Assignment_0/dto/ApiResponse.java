package com.example.Assignment_0.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@NoArgsConstructor(force = true)
@Data
public class ApiResponse<T>{
    private final String message;
    private final Integer statusCode;
    private final T body;
}
