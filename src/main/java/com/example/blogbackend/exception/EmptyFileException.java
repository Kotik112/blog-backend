package com.example.blogbackend.exception;

public class EmptyFileException extends RuntimeException {
	public EmptyFileException(String message) {
		super(message);
	}
}
