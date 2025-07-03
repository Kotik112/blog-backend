package com.example.blogbackend.controller;

import com.example.blogbackend.domain.Role;
import com.example.blogbackend.domain.User;
import com.example.blogbackend.dto.CreateUserRequestDto;
import com.example.blogbackend.dto.LoginRequestDto;
import com.example.blogbackend.repository.UserRepository;
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
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;

  public AuthController(
      UserRepository userRepository,
      PasswordEncoder passwordEncoder,
      AuthenticationManager authenticationManager) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.authenticationManager = authenticationManager;
  }

  @PostMapping("/register")
  public ResponseEntity<String> registerUser(@RequestBody CreateUserRequestDto user) {
    if (userRepository.existsByUsername(user.username().toLowerCase()))
      return ResponseEntity.badRequest().body("Username already exists");

    if (user.username().length() < 3 || user.username().length() > 20)
      return ResponseEntity.badRequest().body("Username must be between 3 and 20 characters");

    User userToSave = new User();
    userToSave.setUsername(user.username().toLowerCase());
    userToSave.setPassword(passwordEncoder.encode(user.password()));
    userToSave.setRole(Role.USER);

    userRepository.save(userToSave);

    return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
  }

  @PostMapping("/login")
  public ResponseEntity<String> loginUser(
      @RequestBody LoginRequestDto request, HttpServletRequest httpRequest) {
    try {
      Authentication authentication =
          authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(
                  request.getUsername().toLowerCase(), request.getPassword()));

      SecurityContextHolder.getContext().setAuthentication(authentication);
      httpRequest.getSession(true); // ensures session is created

      return ResponseEntity.ok("Login successful");
    } catch (AuthenticationException e) {
      logger.warn(
          "Authentication failed for user: {} -> {}", request.getUsername(), e.getMessage());
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
    }
  }
}
