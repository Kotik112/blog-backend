package com.example.blogbackend.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@ResponseStatus(code = BAD_REQUEST)
public class ImageUploadException extends RuntimeException {
	
	public ImageUploadException(String message) {
		super(message);
	}
}
