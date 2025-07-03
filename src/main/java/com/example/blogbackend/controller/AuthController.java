package com.example.blogbackend.controller;

import com.example.blogbackend.domain.Role;
import com.example.blogbackend.domain.User;
import com.example.blogbackend.dto.CreateUserRequestDto;
import com.example.blogbackend.dto.LoginRequestDto;
import com.example.blogbackend.repository.UserRepository;
import com.example.blogbackend.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
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


  public AuthController(
          UserService userService) {
      this.userService = userService;
  }

  @PostMapping("/register")
  public ResponseEntity<String> registerUser(@RequestBody CreateUserRequestDto user) {
    if (user.username().length() < 3 || user.username().length() > 20)
      return ResponseEntity.badRequest().body("Username must be between 3 and 20 characters");
    if (userService.userExists(user.username()))
      return ResponseEntity.badRequest().body("User already exists");

    String response = userService.registerUser(user);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

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
