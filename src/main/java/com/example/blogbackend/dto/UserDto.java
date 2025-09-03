package com.example.blogbackend.dto;

public record UserDto(
    Long id,
    String username,
    String email,
    String firstName,
    String lastName,
    Boolean isActive,
    Boolean isEmailVerified,
    String role,
    String profilePictureUrl,
    String createdAt,
    String updatedAt,
    String lastLoginAt) {
  public static UserDto from(com.example.blogbackend.domain.User user) {
    return new UserDto(
        user.getId(),
        user.getUsername(),
        user.getEmail(),
        user.getFirstName(),
        user.getLastName(),
        user.isActive(),
        user.isEmailVerified(),
        user.getRole().name(),
        user.getProfilePictureUrl(),
        user.getCreatedAt() != null ? user.getCreatedAt().toString() : null,
        user.getUpdatedAt() != null ? user.getUpdatedAt().toString() : null,
        user.getLastLoginAt() != null ? user.getLastLoginAt().toString() : null);
  }
}
