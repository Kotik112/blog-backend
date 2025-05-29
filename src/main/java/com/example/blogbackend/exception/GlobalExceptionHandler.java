package com.example.blogbackend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@SuppressWarnings("unused")
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
	
	@ExceptionHandler(EmptyFileException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiError handleEmptyFileException(EmptyFileException ex, WebRequest request) {
		return handleException(ex, request, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(ImageUploadException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ApiError handleImageUploadException(ImageUploadException ex, WebRequest request) {
		return handleException(ex, request, HttpStatus.INTERNAL_SERVER_ERROR);
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
