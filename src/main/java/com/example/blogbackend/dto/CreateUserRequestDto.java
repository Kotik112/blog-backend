package com.example.blogbackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateUserRequestDto(
    @NotBlank(message = "Username is required") @NotNull String username,
    @NotBlank(message = "Password is required") @NotNull String password) {}
