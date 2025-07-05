package com.example.blogbackend.controller;

import com.example.blogbackend.dto.ApiLoginResponse;
import com.example.blogbackend.dto.CreateUserRequestDto;
import com.example.blogbackend.dto.LoginRequestDto;
import com.example.blogbackend.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Authentication", description = "Endpoints for user authentication and authorization")
@SuppressWarnings("unused")
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
  private final Logger logger = LoggerFactory.getLogger(AuthController.class);
  private final UserService userService;

  public AuthController(UserService userService) {
    this.userService = userService;
  }

  /**
   * Registers a new user with the provided username and password. Validates the username length and
   * checks if the user already exists.
   *
   * @param userRequest the user registration request containing username and password
   * @return a ResponseEntity with the registration status
   */
  @PostMapping("/register")
  public ResponseEntity<String> registerUser(@RequestBody @Valid CreateUserRequestDto userRequest) {
    String result = userService.registerUser(userRequest);

    return switch (result) {
      case "User registered successfully" -> ResponseEntity.status(HttpStatus.CREATED).body(result);
      case "Username must be between 3 and 20 characters",
          "Password must be at least 6 characters long" ->
          ResponseEntity.badRequest().body(result);
      case "User already exists" -> ResponseEntity.status(HttpStatus.CONFLICT).body(result);
      default -> {
        logger.error("Unexpected registration error: {}", result);
        yield ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("Registration failed unexpectedly");
      }
    };
  }

  /**
   * Logs in a user with the provided username and password. Validates the input and checks if the
   * user exists.
   *
   * @param loginRequest the login request containing username and password
   * @param request the HTTP request for context
   * @return a ResponseEntity with the login status
   */
  @PostMapping("/login")
  public ResponseEntity<ApiLoginResponse> loginUser(
      @RequestBody @Valid LoginRequestDto loginRequest, HttpServletRequest request) {
    ApiLoginResponse result = userService.loginUser(loginRequest, request);
    return switch (result.responseEnum()) {
      case SUCCESS -> ResponseEntity.ok(result);
      case USER_NOT_FOUND, INVALID_CREDENTIALS ->
          ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
      default -> ResponseEntity.badRequest().body(result);
    };
  }
}
