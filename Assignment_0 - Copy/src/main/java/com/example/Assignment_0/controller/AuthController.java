package com.example.Assignment_0.controller;

import com.example.Assignment_0.auth.jwt.JwtTokenUtil;
import com.example.Assignment_0.dto.LoginRequest;
import com.example.Assignment_0.dto.RegistrationResponse;
import com.example.Assignment_0.entity.Employee;
import com.example.Assignment_0.service.EmployeeServiceImpl;
import com.example.Assignment_0.dto.ApiResponse;
import com.example.Assignment_0.service.UserDetailsServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/auth/")
@RequiredArgsConstructor
@Tag(name = "Authentication and Authorization")
public class AuthController {

    private final EmployeeServiceImpl employeeService;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserDetailsServiceImpl userDetailsService;

    @Operation(
            description = "Register a new Employee",
            summary = "This endpoint registers a new employee",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            description = "Success",
                            responseCode = "201"
                    )
            }
    )
    @PostMapping(value = "register", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<RegistrationResponse>> registerUser(@Valid @RequestBody Employee employee) {
        try {
            Employee registeredEmployee = employeeService.registerEmployee(employee);

            RegistrationResponse registrationResponse = new RegistrationResponse(
                    registeredEmployee.getId(),
                    registeredEmployee.getName(),
                    registeredEmployee.getEmail(),
                    registeredEmployee.getTechStack(),
                    registeredEmployee.getYearsOfExperience(),
                    registeredEmployee.getYearsInWebknot(),
                    registeredEmployee.getRole()
            );

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>("Employee creation successful.", 201, registrationResponse));

        } catch (Exception e) {
            log.error("Error registering employee: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(e.getMessage(), 500, null));
        }
    }

    @PostMapping(value = "login", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Employee>> loginUser(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        try {
            Employee employeeFromDB = employeeService.findEmployeeByEmail(loginRequest.getEmail());
            if (employeeFromDB == null) {
                throw new RuntimeException("Employee does not exist in the DB");
            }
            if (!employeeService.passwordMatches(loginRequest.getPassword(), employeeFromDB.getPassword())) {
                throw new RuntimeException("Password does not match");
            }

            // Generate JWT tokens
            Map<String, Object> claims = new HashMap<>();
            claims.put("role", employeeFromDB.getRole());
            Map<String, String> tokens = jwtTokenUtil.generateToken(new User(employeeFromDB.getEmail(), employeeFromDB.getPassword(), Collections.emptyList()), claims);

            // Set cookies in the response
            addTokenToCookies(response, "access_token", tokens.get("access_token"));
            addTokenToCookies(response, "refresh_token", tokens.get("refresh_token"));

            // Prepare response with API response format
            ApiResponse<Employee> responseDto = new ApiResponse<>("Login successful", 200, employeeFromDB);
            return ResponseEntity.ok().body(responseDto);

        } catch (Exception e) {
            log.error("Error during login: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(e.getMessage(), 400, null));
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<Map<String, String>>> refreshToken(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            String refreshToken = authorizationHeader.substring(7);
            String userEmail = jwtTokenUtil.extractUserName(refreshToken);

            UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

            if (jwtTokenUtil.isTokenValid(refreshToken, userDetails)) {
                Map<String, String> tokens = jwtTokenUtil.generateToken(userDetails, null);
                return ResponseEntity.ok(new ApiResponse<>("Token refreshed successfully", 200, tokens));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>("Invalid refresh token", 401, null));
            }
        } catch (Exception e) {
            log.error("Error during token refresh: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(e.getMessage(), 400, null));
        }
    }

    private void addTokenToCookies(HttpServletResponse response, String name, String value) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        response.addCookie(cookie);
    }
}
