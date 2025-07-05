package com.example.blogbackend.ultility;

import com.example.blogbackend.dto.CreateUserRequestDto;
import com.example.blogbackend.dto.LoginRequestDto;
import com.example.blogbackend.enums.LoginResponseEnum;
import com.example.blogbackend.exception.LoginFailureException;
import com.example.blogbackend.exception.RegistrationFailureException;

public class ValidationUtility {

  private ValidationUtility() {}

  public static void validateUsername(String username) {
    if (username == null || username.length() < 3 || username.length() > 20) {
      throw new RegistrationFailureException("Username must be between 3 and 20 characters");
    }
  }

  public static void validatePassword(String password) {
    if (password == null || password.length() < 6) {
      throw new RegistrationFailureException("Password must be at least 6 characters long");
    }
  }

  /**
   * Validates the given CreateUserRequestDto. Ensures null-safe access and checks for username and
   * password validity.
   *
   * @param userRequest the user request to validate
   * @throws RegistrationFailureException if validation fails
   */
  public static void validateUserRequest(CreateUserRequestDto userRequest) {
    if (userRequest == null) {
      throw new RegistrationFailureException(LoginResponseEnum.REQUEST_BODY_MISSING.getMessage());
    }
    validateUsername(userRequest.username());
    validatePassword(userRequest.password());
  }

  /**
   * Validates the given LoginRequestDto. Ensures null-safe access.
   *
   * @param loginRequest the login request to validate
   * @throws LoginFailureException if validation fails
   */
  public static void validateLoginRequest(LoginRequestDto loginRequest) {
    if (loginRequest == null) {
      throw new LoginFailureException(LoginResponseEnum.REQUEST_BODY_MISSING.getMessage());
    }
    String username = loginRequest.getUsername();
    String password = loginRequest.getPassword();
    if (username == null || username.isBlank() || password == null || password.isBlank()) {
      throw new LoginFailureException(LoginResponseEnum.EMPTY_CREDENTIALS.getMessage());
    }
  }
}
