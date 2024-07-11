package com.example.Assignment_0.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                      name = "Tahmeer Pasha",
                        email = "tahmeer.pasha@webknot.in",
                        url = "https://tahmeer.tech"
                ),
                description = "This is a Bench Management project for Webknot Technologies",
                title = "Bench Management Backend",
                version = "1.0"
        )
)
@SecurityScheme(
        name = "JWT Authentication",
        description = "Authentication is performed using access token and refresh token",
        scheme = "Bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.COOKIE
)
public class OpenApiConfig {
}
