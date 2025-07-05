package com.example.blogbackend.controller;

import com.example.blogbackend.dto.CreateUserRequestDto;
import com.example.blogbackend.dto.LoginRequestDto;
import com.example.blogbackend.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
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
   * @param user the user registration request containing username and password
   * @return a ResponseEntity with the registration status
   */
  @PostMapping("/register")
  public ResponseEntity<String> registerUser(@RequestBody CreateUserRequestDto user) {
    if (user.username().length() < 3 || user.username().length() > 20)
      return ResponseEntity.badRequest().body("Username must be between 3 and 20 characters");
    if (userService.userExists(user.username()))
      return ResponseEntity.badRequest().body("User already exists");

    String response = userService.registerUser(user);
    if (response.equals("User registered successfully")) {
      return ResponseEntity.status(HttpStatus.CREATED).body(response);
    } else {
      logger.warn("Registration failed: {}", response);
      return ResponseEntity.badRequest().body(response);
    }
  }

  /**
   * Logs in a user with the provided username and password. Validates the input and checks if the
   * user exists.
   *
   * @param request the login request containing username and password
   * @param httpRequest the HTTP request for context
   * @return a ResponseEntity with the login status
   */
  @PostMapping("/login")
  public ResponseEntity<String> loginUser(
      @RequestBody LoginRequestDto request, HttpServletRequest httpRequest) {
    String response = userService.loginUser(request, httpRequest);
    if (response.equals("Login successful")) {
      return ResponseEntity.ok(response);
    } else {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }
  }
}
