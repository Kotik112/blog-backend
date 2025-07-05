package com.example.blogbackend.exception;

public class LoginFailureException extends RuntimeException {
  public LoginFailureException(String message) {
    super(message);
  }
}
