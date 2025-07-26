package com.example.blogbackend.service;

import com.example.blogbackend.domain.User;
import com.example.blogbackend.dto.ApiLoginResponse;
import com.example.blogbackend.dto.CreateUserRequestDto;
import com.example.blogbackend.dto.LoginRequestDto;
import com.example.blogbackend.enums.LoginResponseEnum;
import com.example.blogbackend.enums.Role;
import com.example.blogbackend.exception.UserAlreadyExistsException;
import com.example.blogbackend.repository.UserRepository;
import com.example.blogbackend.utility.ValidationUtility;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
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
    ValidationUtility.validateUserRequest(userRequest);

    String normalizedUsername = userRequest.username().toLowerCase();
    if (userRepository.existsByUsername(normalizedUsername)) {
      logger.warn("Registration attempt with existing username: {}", userRequest.username());
      throw new UserAlreadyExistsException("User already exists");
    }

    User user = new User();
    user.setUsername(normalizedUsername);
    user.setPassword(passwordEncoder.encode(userRequest.password()));
    user.setRole(Role.USER);
    User savedUser = userRepository.save(user);
    logger.info("User registered successfully: {}", savedUser.getUsername());
    return "User registered successfully";
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
}
