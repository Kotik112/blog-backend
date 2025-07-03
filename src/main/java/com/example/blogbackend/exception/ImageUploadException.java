package com.example.blogbackend.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = BAD_REQUEST)
public class ImageUploadException extends RuntimeException {

  public ImageUploadException(String message) {
    super(message);
  }
}
