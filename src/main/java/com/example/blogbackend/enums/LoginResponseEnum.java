package com.example.blogbackend.enums;

import lombok.Getter;

/**
 * Enum representing various login response messages. Used to provide consistent feedback for login
 * operations.
 */
@Getter
public enum LoginResponseEnum {
  SUCCESS("Login successful"),
  USER_NOT_FOUND("User does not exist"),
  INVALID_CREDENTIALS("Invalid credentials"),
  EMPTY_CREDENTIALS("Username and password must not be empty"),
  USERNAME_LENGTH_ERROR("Username must be between 3 and 20 characters"),
  PASSWORD_LENGTH_ERROR("Password must be at least 6 characters long"),
  ;

  private final String message;

  LoginResponseEnum(String message) {
    this.message = message;
  }
}
