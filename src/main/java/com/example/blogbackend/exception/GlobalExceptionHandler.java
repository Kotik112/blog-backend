package com.example.blogbackend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(BlogPostNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ApiError handleBlogPostNotFoundException(BlogPostNotFoundException ex, WebRequest request) {
		return handleException(ex, request, HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(CommentNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ApiError handleCommentNotFoundException(CommentNotFoundException ex, WebRequest request) {
		return handleException(ex, request, HttpStatus.NOT_FOUND);
	}
	
	private ApiError handleException(Exception ex, WebRequest request, HttpStatus status) {
		ApiError apiError = new ApiError();
		apiError.setTimestamp(LocalDateTime.now());
		apiError.setStatus(status.value());
		apiError.setError(status.getReasonPhrase());
		apiError.setMessage(ex.getMessage());
		apiError.setPath(request.getDescription(false));
		return apiError;
	}
}
