package com.example.Assignment_0.auth.jwt;

import com.example.Assignment_0.service.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtTokenUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private final Logger logger = LoggerFactory.getLogger(JwtTokenFilter.class);

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String jwtToken = getJwtTokenFromHeader(request).orElseGet(() -> getJwtTokenFromCookies(request.getCookies()));

            if (jwtToken != null) {
                String userEmail = jwtTokenUtil.extractUserName(jwtToken);

                if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

                    if (userDetails != null && jwtTokenUtil.isTokenValid(jwtToken, userDetails)) {
                        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities()
                        );
                        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                        request.setAttribute("userDetails", userDetails);
                        logger.debug("Authenticated user");
                    } else {
                        logger.debug("UserDetails is null or JWT token is invalid.");
                    }
                } else {
                    logger.debug("User email is null or user is already authenticated.");
                }
            }

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            logger.error("Error during JWT authentication", e);
            filterChain.doFilter(request, response);
        }
    }

    private Optional<String> getJwtTokenFromHeader(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader("Authorization"))
                .filter(authHeader -> authHeader.startsWith("Bearer "))
                .map(authHeader -> {
                    logger.debug("Access Token found in the HTTP header.");
                    return authHeader.substring(7);
                });
    }

    private String getJwtTokenFromCookies(Cookie[] cookies) {
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("access_token".equals(cookie.getName())) {
                    logger.debug("Access Token found in the cookies.");
                    return cookie.getValue();
                }
            }
        }
        logger.debug("Access Token not found in the cookies.");
        return null;
    }
}
