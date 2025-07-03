package com.example.blogbackend.service;

import com.example.blogbackend.domain.Role;
import com.example.blogbackend.domain.User;
import com.example.blogbackend.dto.CreateUserRequestDto;
import com.example.blogbackend.dto.LoginRequestDto;
import com.example.blogbackend.repository.UserRepository;
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
import org.springframework.stereotype.Service;


@Service
public class UserService {
    private final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    public boolean userExists(String username) {
        return userRepository.existsByUsername(username);
    }

    public String registerUser(CreateUserRequestDto userRequest) {
        User user = new User();
        user.setUsername(userRequest.username());
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
        if (!userExists(loginRequestDto.getUsername())) {
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
                    "Authentication failed for user: {} -> {}", loginRequestDto.getUsername(), e.getMessage());
            return "Invalid credentials";
        }
    }
}
