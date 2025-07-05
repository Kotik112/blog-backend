package com.example.blogbackend.exception;

public class RegistrationFailureException extends RuntimeException {
  public RegistrationFailureException(String message) {
    super(message);
  }
}
