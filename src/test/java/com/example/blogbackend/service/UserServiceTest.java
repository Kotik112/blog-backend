package com.example.blogbackend.service;

import static org.mockito.Mockito.*;

import com.example.blogbackend.domain.Role;
import com.example.blogbackend.domain.User;
import com.example.blogbackend.dto.ApiLoginResponse;
import com.example.blogbackend.dto.CreateUserRequestDto;
import com.example.blogbackend.dto.LoginRequestDto;
import com.example.blogbackend.enums.LoginResponseEnum;
import com.example.blogbackend.exception.LoginFailureException;
import com.example.blogbackend.exception.RegistrationFailureException;
import com.example.blogbackend.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @InjectMocks UserService userService;

  @Mock PasswordEncoder passwordEncoder;

  @Mock UserRepository userRepository;

  @Mock AuthenticationManager authenticationManager;

  @Test
  void test_userExists_success() {
    String username = "testuser";
    when(userRepository.existsByUsername(username)).thenReturn(true);
    boolean exists = userService.userExists(username);
    Assertions.assertTrue(exists);

    verify(userRepository, times(1)).existsByUsername(username);
  }

  @Test
  void test_userExists_notFound() {
    String username = "nonexistentuser";
    when(userRepository.existsByUsername(username)).thenReturn(false);
    boolean exists = userService.userExists(username);
    Assertions.assertFalse(exists);

    verify(userRepository, times(1)).existsByUsername(username);
  }

  @Test
  void test_userExists_nullUsername() {
    String username = null;
    boolean exists = userService.userExists(username);
    Assertions.assertFalse(exists);
    verify(userRepository, never()).existsByUsername(any());
  }

  @Test
  void test_userExists_emptyUsername() {
    String username = "";
    boolean exists = userService.userExists(username);
    Assertions.assertFalse(exists);
    verify(userRepository, never()).existsByUsername(any());
  }

  @Test
  void test_registerUser_success() {
    CreateUserRequestDto userRequest = new CreateUserRequestDto("newuser", "password123");
    User savedUser = new User();
    savedUser.setUsername("newuser");
    savedUser.setPassword("encodedPassword");
    savedUser.setRole(Role.USER);

    when(userRepository.existsByUsername("newuser")).thenReturn(false);
    when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
    when(userRepository.save(any())).thenReturn(savedUser);

    String result = userService.registerUser(userRequest);
    Assertions.assertEquals("User registered successfully", result);

    verify(userRepository, times(1)).existsByUsername("newuser");
    verify(passwordEncoder, times(1)).encode("password123");
    verify(userRepository, times(1)).save(any(User.class));
  }

  @Test
  void test_registerUser_usernameTooShort() {
    CreateUserRequestDto userRequest = new CreateUserRequestDto("nu", "password123");
    RegistrationFailureException ex =
        Assertions.assertThrows(
            RegistrationFailureException.class, () -> userService.registerUser(userRequest));
    Assertions.assertEquals("Username must be between 3 and 20 characters", ex.getMessage());
  }

  @Test
  void test_registerUser_usernameTooLong() {
    CreateUserRequestDto userRequest =
        new CreateUserRequestDto("thisusernameistoolong", "password123");
    RegistrationFailureException ex =
        Assertions.assertThrows(
            RegistrationFailureException.class, () -> userService.registerUser(userRequest));
    Assertions.assertEquals("Username must be between 3 and 20 characters", ex.getMessage());
  }

  @Test
  void test_registerUser_passwordTooShort() {
    CreateUserRequestDto userRequest = new CreateUserRequestDto("newuser", "123");
    RegistrationFailureException ex =
        Assertions.assertThrows(
            RegistrationFailureException.class, () -> userService.registerUser(userRequest));
    Assertions.assertEquals("Password must be at least 6 characters long", ex.getMessage());
  }

  @Test
  void test_registerUser_passwordNull_throwsException() {
    CreateUserRequestDto userRequest = new CreateUserRequestDto("newuser", null);
    RegistrationFailureException ex =
        Assertions.assertThrows(
            RegistrationFailureException.class, () -> userService.registerUser(userRequest));
    Assertions.assertEquals("Password must be at least 6 characters long", ex.getMessage());
  }

  @Test
  void test_registerUser_createUserRequestDtoNull_throwsException() {
    CreateUserRequestDto userRequest = null;
    RegistrationFailureException ex =
        Assertions.assertThrows(
            RegistrationFailureException.class, () -> userService.registerUser(userRequest));
    Assertions.assertEquals("Request body is missing", ex.getMessage());
  }

  // Login tests
  @Test
  void test_loginUser_success() {
    String username = "testuser";

    LoginRequestDto loginRequest = new LoginRequestDto(username, "testPassword");
    Authentication authentication = mock(Authentication.class);
    HttpServletRequest httpRequest = spy(HttpServletRequest.class);
    HttpSession session = mock(HttpSession.class);

    when(userRepository.existsByUsername(username)).thenReturn(true);
    when(authenticationManager.authenticate(any())).thenReturn(authentication);
    when(httpRequest.getSession(true)).thenReturn(session);

    ApiLoginResponse result = userService.loginUser(loginRequest, httpRequest);
    Assertions.assertEquals(LoginResponseEnum.SUCCESS.getMessage(), result.message());

    verify(authenticationManager, times(1)).authenticate(any());
    verify(userRepository, times(1)).existsByUsername(username);
  }

  @Test
  void test_loginUser_userNotFound() {
    LoginRequestDto loginRequest = new LoginRequestDto("nonexistentuser", "testPassword");
    HttpServletRequest httpRequest = mock(HttpServletRequest.class);

    when(userRepository.existsByUsername("nonexistentuser")).thenReturn(false);

    ApiLoginResponse result = userService.loginUser(loginRequest, httpRequest);
    Assertions.assertEquals(LoginResponseEnum.USER_NOT_FOUND.getMessage(), result.message());

    verify(userRepository, times(1)).existsByUsername("nonexistentuser");
    verify(authenticationManager, never()).authenticate(any());
  }

  @Test
  void test_loginUser_usernameNull() {
    LoginRequestDto loginRequest = new LoginRequestDto(null, "testPassword");
    HttpServletRequest httpRequest = mock(HttpServletRequest.class);

    LoginFailureException ex =
        Assertions.assertThrows(
            LoginFailureException.class, () -> userService.loginUser(loginRequest, httpRequest));
    Assertions.assertEquals(LoginResponseEnum.EMPTY_CREDENTIALS.getMessage(), ex.getMessage());

    verify(userRepository, never()).existsByUsername(any());
    verify(authenticationManager, never()).authenticate(any());
  }

  @Test
  void test_loginUser_passwordNull() {
    LoginRequestDto loginRequest = new LoginRequestDto("test123", null);
    HttpServletRequest httpRequest = mock(HttpServletRequest.class);
    LoginFailureException ex =
        Assertions.assertThrows(
            LoginFailureException.class, () -> userService.loginUser(loginRequest, httpRequest));
    Assertions.assertEquals(LoginResponseEnum.EMPTY_CREDENTIALS.getMessage(), ex.getMessage());
    verify(userRepository, never()).existsByUsername(any());
    verify(authenticationManager, never()).authenticate(any());
  }

  @Test
  void test_loginUser_loginRequestDtoNull() {
    LoginRequestDto loginRequest = null;
    HttpServletRequest httpRequest = mock(HttpServletRequest.class);
    LoginFailureException ex =
        Assertions.assertThrows(
            LoginFailureException.class, () -> userService.loginUser(loginRequest, httpRequest));
    Assertions.assertEquals(LoginResponseEnum.REQUEST_BODY_MISSING.getMessage(), ex.getMessage());
  }
}
