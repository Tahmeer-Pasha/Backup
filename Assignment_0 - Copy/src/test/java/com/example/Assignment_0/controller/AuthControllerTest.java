package com.example.Assignment_0.controller;

import com.example.Assignment_0.auth.jwt.JwtTokenUtil;
import com.example.Assignment_0.dto.ApiResponse;
import com.example.Assignment_0.dto.LoginRequest;
import com.example.Assignment_0.entity.Employee;
import com.example.Assignment_0.service.EmployeeServiceImpl;
import com.example.Assignment_0.service.UserDetailsServiceImpl;
import com.example.Assignment_0.utils.enums.Role;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.Assignment_0.util.ConvertToJson.convertToJSON;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeServiceImpl employeeService;

    @MockBean
    private JwtTokenUtil jwtTokenUtil;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    private Employee employee;
    private LoginRequest loginRequest;
    Map<String, String> tokens;

    @BeforeEach
    void setUp() {
        List<String> techStacks = new ArrayList<>();
        techStacks.add("React.js");
        techStacks.add("Next.js");
        techStacks.add("Spring boot");
        techStacks.add("Node.js");



        // Mock employee data
        employee = Employee.builder()
                .name("Test User")
                .email("test@user.com")
                .password("hashed_password")
                .techStack(techStacks)
                .yearsInWebknot(5.0)
                .yearsOfExperience(5)
                .role(Role.ADMIN)
                .build();

        // Mock login request data
        loginRequest = LoginRequest.builder().email("test@user.com").password("password").build();

        // Mock JWT tokens
        tokens = new HashMap<>();
        tokens.put("access_token", "mock_access_token");
        tokens.put("refresh_token", "mock_refresh_token");

    }

    @Test
    void shouldRegisterUser() throws Exception {
        String jsonRequest = convertToJSON(employee);

        when(employeeService.registerEmployee(employee)).thenReturn(employee);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andDo(print()).andExpect(status().isCreated());
    }

    @Test
    void loginUser() throws Exception {
        String jsonRequest = convertToJSON(loginRequest);
        // Mock service calls
        when(employeeService.findEmployeeByEmail(loginRequest.getEmail())).thenReturn(employee);
        when(employeeService.passwordMatches(loginRequest.getPassword(), employee.getPassword())).thenReturn(true);
        when(jwtTokenUtil.generateToken(any(), any())).thenReturn(tokens);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                        .andExpect(status().isOk());
    }

    @Test
    void refreshToken() {
        // Implement this test based on your refresh token logic
    }
}
