package com.example.blogbackend.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.blogbackend.dto.CreateUserRequestDto;
import com.example.blogbackend.dto.LoginRequestDto;
import com.example.blogbackend.utils.SpringBootComponentTest;
import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

class AuthControllerIntegrationTest extends SpringBootComponentTest {

  @Autowired private MockMvc mvc;

  @Test
  void when_registerUser_then_userIsRegistered() throws Exception {
    CreateUserRequestDto user = new CreateUserRequestDto("test", "testPassword");

    MvcResult result =
        mvc.perform(
                post(BASE_AUTH_URL + "/register")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(user)))
            .andExpect(status().isCreated())
            .andReturn();

    String response = result.getResponse().getContentAsString();
    Assertions.assertEquals(
        "User registered successfully",
        response,
        "Expected user registration success message, got: " + response);
  }

  @Test
  void when_registerUserWithShortUsername_then_badRequest() throws Exception {
    CreateUserRequestDto user = new CreateUserRequestDto("ab", "testPassword");

    MvcResult result =
        mvc.perform(
                post(BASE_AUTH_URL + "/register")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(user)))
            .andExpect(status().isBadRequest())
            .andReturn();

    String response = result.getResponse().getContentAsString();
    Assertions.assertEquals(
        "Username must be between 3 and 20 characters",
        response,
        "Expected username length error, got: " + response);
  }

  @Test
  void when_registerUserWithExistingUsername_then_badRequest() throws Exception {
    CreateUserRequestDto user = new CreateUserRequestDto("existingUser", "testPassword");

    // First registration should succeed
    MvcResult successResult =
        mvc.perform(
                post(BASE_AUTH_URL + "/register")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(user)))
            .andExpect(status().isCreated())
            .andReturn();

    String successResponse = successResult.getResponse().getContentAsString();
    Assertions.assertEquals(
        "User registered successfully",
        successResponse,
        "Expected user registration success message, got: " + successResponse);

    // Second registration should fail
    MvcResult failedResult =
        mvc.perform(
                post(BASE_AUTH_URL + "/register")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(user)))
            .andExpect(status().isBadRequest())
            .andReturn();

    String response = failedResult.getResponse().getContentAsString();
    Assertions.assertEquals(
        "User already exists", response, "Expected user already exists error, got: " + response);
  }

  // Login tests
  @Test
  void when_loginUser_then_userIsLoggedIn() throws Exception {
    CreateUserRequestDto user = new CreateUserRequestDto("loginUser", "loginPassword");

    // First register the user
    mvc.perform(
            post(BASE_AUTH_URL + "/register")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(user)))
        .andExpect(status().isCreated());

    LoginRequestDto loginRequestDto = new LoginRequestDto(user.username(), user.password());
    // Now login the user
    MvcResult result =
        mvc.perform(
                post(BASE_AUTH_URL + "/login")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(loginRequestDto)))
            .andExpect(status().isOk())
            .andReturn();

    String response = result.getResponse().getContentAsString();
    Assertions.assertEquals(
        "Login successful", response, "Expected login success message, got: " + response);
  }

  @Test
  void when_loginUserWithNonExistingUser_then_unauthorized() throws Exception {
    LoginRequestDto user = new LoginRequestDto("nonExistingUser", "somePassword");

    MvcResult result =
        mvc.perform(
                post(BASE_AUTH_URL + "/login")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(user)))
            .andExpect(status().isUnauthorized())
            .andReturn();

    String response = result.getResponse().getContentAsString();
    Assertions.assertEquals(
        "User does not exist", response, "Expected user does not exist error, got: " + response);
  }

  @Test
  void when_loginUserWithInvalidCredentials_then_unauthorized() throws Exception {
    CreateUserRequestDto user = new CreateUserRequestDto("test1", "correctPassword");
    // First register the user
    val mvcResult =
        mvc.perform(
                post(BASE_AUTH_URL + "/register")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(user)))
            .andExpect(status().isCreated())
            .andReturn();

    String response = mvcResult.getResponse().getContentAsString();
    Assertions.assertEquals(
        "User registered successfully",
        response,
        "Expected user registration success message, got: " + response);

    // Now try to log in with wrong password
    LoginRequestDto loginRequest = new LoginRequestDto(user.username(), "wrongPassword");

    MvcResult result =
        mvc.perform(
                post(BASE_AUTH_URL + "/login")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isUnauthorized())
            .andReturn();

    String responseBody = result.getResponse().getContentAsString();
    Assertions.assertEquals(
        "Invalid credentials",
        responseBody,
        "Expected invalid credentials error, got: " + responseBody);
  }
}
