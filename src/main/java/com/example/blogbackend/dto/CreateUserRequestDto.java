package com.example.blogbackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateUserRequestDto(
    @NotBlank(message = "Username is required")
        @NotNull
        @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
        String username,
    @NotBlank(message = "Password is required")
        @NotNull
        @Size(min = 6, message = "Password must be at least 6 characters long")
        String password,
    @NotBlank(message = "Email is required")
        @NotNull
        @Size(max = 255, message = "Email must not exceed 255 characters")
        String email,
    @Size(max = 100, message = "First name must not exceed 100 characters") String firstName,
    @Size(max = 100, message = "Last name must not exceed 100 characters") String lastName) {}
