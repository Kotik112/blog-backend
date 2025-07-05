package com.example.blogbackend;

import com.example.blogbackend.domain.Role;
import com.example.blogbackend.domain.User;
import com.example.blogbackend.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Initializes a test user in the database when the "test" profile is active. This user can be used
 * for integration tests that require authentication.
 */
@SuppressWarnings("unused")
@Configuration
@Profile("test")
public class TestUserInitializer {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public TestUserInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @PostConstruct
  public void registerTestUser() {
    if (userRepository.existsByUsername("testuser")) return;

    User user = new User();
    user.setUsername("testuser");
    user.setPassword(passwordEncoder.encode("testPassword"));
    user.setRole(Role.USER);

    userRepository.save(user);
  }
}
