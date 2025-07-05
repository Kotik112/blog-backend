package com.example.blogbackend.service;

import com.example.blogbackend.domain.Role;
import com.example.blogbackend.domain.User;
import com.example.blogbackend.dto.CreateUserRequestDto;
import com.example.blogbackend.dto.LoginRequestDto;
import com.example.blogbackend.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
  private final Logger logger = LoggerFactory.getLogger(UserService.class);
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;

  public UserService(
      UserRepository userRepository,
      PasswordEncoder passwordEncoder,
      AuthenticationManager authenticationManager) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.authenticationManager = authenticationManager;
  }

  public boolean userExists(String username) {
    if (username == null || username.isEmpty()) {
      logger.warn("Username is null or empty");
      return false;
    }
    return userRepository.existsByUsername(username);
  }

  public String registerUser(CreateUserRequestDto userRequest) {
    if (userRepository.existsByUsername(userRequest.username().toLowerCase())) {
      logger.warn("Registration attempt with existing username: {}", userRequest.username());
      return "User already exists";
    }
    if (userRequest.username().length() < 3 || userRequest.username().length() > 20) {
      logger.warn("Registration attempt with invalid username length: {}", userRequest.username());
      return "Username must be between 3 and 20 characters";
    }
    if (userRequest.password() == null || userRequest.password().length() < 6) {
      logger.warn("Registration attempt with invalid password length");
      return "Password must be at least 6 characters long";
    }

    User user = new User();
    user.setUsername(userRequest.username().toLowerCase());
    user.setPassword(passwordEncoder.encode(userRequest.password()));
    user.setRole(Role.USER);
    User savedUser = userRepository.save(user);
    logger.info("User registered successfully: {}", savedUser.getUsername());
    return "User registered successfully";
  }

  public String loginUser(LoginRequestDto loginRequestDto, HttpServletRequest httpRequest) {
    if (loginRequestDto.getUsername() == null || loginRequestDto.getPassword() == null) {
      logger.warn("Login attempt with missing username or password");
      return "Username and password must not be empty";
    }
    if (!userExists(loginRequestDto.getUsername().toLowerCase())) {
      logger.warn("Login attempt with non-existing user: {}", loginRequestDto.getUsername());
      return "User does not exist";
    }
    try {
      Authentication authentication =
          authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(
                  loginRequestDto.getUsername().toLowerCase(), loginRequestDto.getPassword()));

      SecurityContextHolder.getContext().setAuthentication(authentication);
      httpRequest.getSession(true); // ensures session is created

      return "Login successful";
    } catch (AuthenticationException e) {
      logger.warn(
          "Authentication failed for user: {} -> {}",
          loginRequestDto.getUsername(),
          e.getMessage());
      return "Invalid credentials";
    }
  }
}
