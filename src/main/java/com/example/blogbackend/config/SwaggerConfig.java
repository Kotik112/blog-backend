package com.example.blogbackend.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Blog Post Backend API",
                version = "1.0",
                description = "API for managing blog posts, including creation, retrieval, commenting " +
                        "and image handling.",
                contact = @Contact(name = "Arman Iqbal", email = "armaniqbal@gmail.com")
        )
)
@SuppressWarnings("unused")
public class SwaggerConfig { }
