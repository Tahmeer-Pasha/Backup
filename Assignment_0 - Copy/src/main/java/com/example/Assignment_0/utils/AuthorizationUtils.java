package com.example.Assignment_0.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.userdetails.UserDetails;

public class AuthorizationUtils {

    public static boolean isAuthorized(HttpServletRequest request) {
        UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");
        return userDetails != null && userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
    }
}