package com.example.Assignment_0.service;

import com.example.Assignment_0.entity.Employee;
import com.example.Assignment_0.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final EmployeeRepository employeeRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Employee> optionalEmployee = employeeRepository.findByEmail(email);
        if (optionalEmployee.isEmpty()) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }
        Employee employee = optionalEmployee.get();

        // Extract role from Employee entity
        String role = String.valueOf(employee.getRole()); // Assuming getRole returns a string representing the role

        // Create GrantedAuthority from role
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);

        // Create UserDetails with username, password, and authority
        return new User(employee.getEmail(), employee.getPassword(), Collections.singleton(authority));
    }
}
