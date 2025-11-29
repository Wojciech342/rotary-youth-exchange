package org.rotary.exchange.backend.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI/Swagger Configuration for the Rotary Youth Exchange API.
 * 
 * Access the Swagger UI at: http://localhost:8080/swagger-ui/index.html
 * Access the OpenAPI spec at: http://localhost:8080/v3/api-docs
 */
@Configuration
public class OpenApiConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI rotaryYouthExchangeOpenAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Development Server")
                ))
                .tags(List.of(
                        new Tag().name("Authentication").description("User authentication and session management"),
                        new Tag().name("Camps").description("Camp management operations"),
                        new Tag().name("Camp Templates").description("Reusable camp template management"),
                        new Tag().name("Coordinators").description("Coordinator profile management"),
                        new Tag().name("Countries").description("Country and nested district operations"),
                        new Tag().name("Districts").description("Rotary district operations"),
                        new Tag().name("District Status").description("Per-district camp availability status"),
                        new Tag().name("Files").description("File upload and retrieval for images and PDFs")
                ))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Enter your JWT access token. Obtain it from POST /api/auth/login")
                        )
                )
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }

    private Info apiInfo() {
        return new Info()
                .title("Rotary Youth Exchange API")
                .version("1.0.0")
                .description("""
                        REST API for the Rotary Youth Exchange Camp Management System.
                        
                        ## Overview
                        This API allows:
                        - **Students** (unauthenticated): Browse available camps
                        - **Coordinators**: Manage their own camps, upload files
                        - **Admins**: Full system management
                        
                        ## Authentication
                        Most endpoints require JWT authentication. To authenticate:
                        1. Call `POST /api/auth/login` with email and password
                        2. Use the returned `accessToken` in the Authorization header: `Bearer {token}`
                        3. Access tokens expire in 15 minutes; use `POST /api/auth/refresh` to get a new one
                        
                        ## Key Concepts
                        - **Camp Template**: Reusable camp definition (name, description, age limits)
                        - **Camp Instance**: A specific edition of a camp (dates, price, coordinator)
                        - **District Status**: Camp availability can be customized per district
                        """);
    }
}
