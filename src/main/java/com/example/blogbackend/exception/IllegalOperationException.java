package com.example.blogbackend.exception;

public class IllegalOperationException extends RuntimeException {
  public IllegalOperationException(String message) {
    super(message);
  }
}
