package com.example.blogbackend.service;

import com.example.blogbackend.domain.User;
import com.example.blogbackend.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthenticatedUserService {

  private final UserRepository userRepository;

  public AuthenticatedUserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public User getAuthenticatedUser(String username) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    return userRepository
        .findByUsername(auth.getName())
        .orElseThrow(() -> new UsernameNotFoundException("User not found: " + auth.getName()));
  }
}
