package com.example.blogbackend.service;

import com.example.blogbackend.domain.User;
import com.example.blogbackend.dto.ApiLoginResponse;
import com.example.blogbackend.dto.CreateUserRequestDto;
import com.example.blogbackend.dto.LoginRequestDto;
import com.example.blogbackend.dto.UserDto;
import com.example.blogbackend.enums.LoginResponseEnum;
import com.example.blogbackend.enums.Role;
import com.example.blogbackend.exception.UserAlreadyExistsException;
import com.example.blogbackend.provider.TimeProvider;
import com.example.blogbackend.repository.UserRepository;
import com.example.blogbackend.utility.ValidationUtility;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {
  private final Logger logger = LoggerFactory.getLogger(UserService.class);
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final TimeProvider timeProvider;

  public UserService(
      UserRepository userRepository,
      PasswordEncoder passwordEncoder,
      AuthenticationManager authenticationManager,
      TimeProvider timeProvider) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.authenticationManager = authenticationManager;
    this.timeProvider = timeProvider;
  }

  public boolean userExists(String username) {
    if (username == null || username.isEmpty()) {
      logger.warn("Username is null or empty");
      return false;
    }
    return userRepository.existsByUsername(username);
  }

  public String registerUser(CreateUserRequestDto userRequest) {
    ValidationUtility.validateUserRequest(userRequest);

    String normalizedUsername = userRequest.username().toLowerCase();
    if (userRepository.existsByUsername(normalizedUsername)) {
      logger.warn("Registration attempt with existing username: {}", userRequest.username());
      throw new UserAlreadyExistsException("User already exists");
    }
    if (userRepository.existsByEmail(userRequest.email())) {
      logger.warn("Registration attempt with existing email: {}", userRequest.email());
      throw new UserAlreadyExistsException("Email already registered");
    }

    User user = new User();
    user.setUsername(normalizedUsername);
    user.setPassword(passwordEncoder.encode(userRequest.password()));
    user.setEmail(userRequest.email().toLowerCase());
    user.setFirstName(userRequest.firstName());
    user.setLastName(userRequest.lastName());
    user.setRole(Role.USER); // default role
    user.setActive(true);
    user.setEmailVerified(false);
    user.setCreatedAt(timeProvider.getNow());

    User savedUser = userRepository.save(user);
    logger.info("User registered successfully: {}", savedUser.getUsername());
    return "User registered successfully";
  }

  public Page<UserDto> getAllUsers(int page, int size) {
    Page<User> usersPage = userRepository.findAll(PageRequest.of(page, size));
    return usersPage.map(UserDto::from);
  }

  public ApiLoginResponse loginUser(
      LoginRequestDto loginRequestDto, HttpServletRequest httpRequest) {
    ValidationUtility.validateLoginRequest(loginRequestDto);

    String normalizedUsername = loginRequestDto.getUsername().toLowerCase();
    if (!userExists(normalizedUsername)) {
      logger.warn("Login attempt with non-existing user: {}", normalizedUsername);
      return new ApiLoginResponse(null, null, null, null, LoginResponseEnum.USER_NOT_FOUND);
    }
    try {
      Authentication authentication =
          authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(
                  normalizedUsername, loginRequestDto.getPassword()));

      SecurityContext context = SecurityContextHolder.getContext();
      context.setAuthentication(authentication);

      HttpSession session = httpRequest.getSession(true); // create or reuse session
      session.setAttribute(
          HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);

      String sessionId = session.getId();
      String ipAddress = httpRequest.getRemoteAddr();
      List<String> roles =
          authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
      return new ApiLoginResponse(
          normalizedUsername, roles, sessionId, ipAddress, LoginResponseEnum.SUCCESS);
    } catch (AuthenticationException e) {
      logger.warn("Authentication failed for user: {} -> {}", normalizedUsername, e.getMessage());
      return new ApiLoginResponse(null, null, null, null, LoginResponseEnum.INVALID_CREDENTIALS);
    }
  }

  public void logoutUser(HttpServletRequest request, HttpServletResponse response) {
    // Clear the security context
    SecurityContextHolder.clearContext();

    // Invalidate the session if it exists
    HttpSession session = request.getSession(false);
    if (session != null) {
      logger.info("Invalidating session: {}", session.getId());
      session.invalidate();
    }

    // Expire the JSESSIONID cookie
    Cookie cookie = new Cookie("JSESSIONID", null);
    cookie.setPath("/");
    cookie.setHttpOnly(true);
    cookie.setMaxAge(0);
    response.addCookie(cookie);
  }

  public UserDto updateUserRole(String username, String role) {
    if (!isValidRole(role)) {
      throw new IllegalArgumentException("Invalid role: " + role);
    }
    User user =
        userRepository
            .findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

    Role newRole = parseRole(role);
    if (user.getRole() == newRole) {
      logger.info("User: {} already has role: {}", username, newRole);
      return UserDto.from(user);
    }
    user.setRole(newRole);
    User updatedUser = userRepository.save(user);
    logger.info("Updated role for user: {} to {}", username, newRole);
    return UserDto.from(updatedUser);
  }

  public void deleteUser(String username) {
    User user =
        userRepository
            .findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    userRepository.delete(user);
  }

  private boolean isValidRole(String role) {
    if (role == null || role.isBlank()) return false;
    return Arrays.stream(Role.values()).anyMatch(r -> r.name().equalsIgnoreCase(role.trim()));
  }

  private Role parseRole(String role) {
    String normalized = role.trim().toUpperCase();
    if (normalized.startsWith("ROLE_")) {
      normalized = normalized.substring(5);
    }
    try {
      return Role.valueOf(normalized);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Invalid role: " + role);
    }
  }
}
