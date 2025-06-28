package com.example.blogbackend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

/**
 * Global exception handler for the blog backend application.
 * Handles specific exceptions and returns appropriate error responses.
 */
@SuppressWarnings("unused")
@RestControllerAdvice
public class GlobalExceptionHandler {

	/**
	 * Handles all exceptions that are not explicitly handled by other methods.
	 *
	 * @param ex the exception that was thrown
	 * @param request the current web request
	 * @return an ApiError object containing error details
	 */
	@ExceptionHandler(BlogPostNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ApiError handleBlogPostNotFoundException(BlogPostNotFoundException ex, WebRequest request) {
		return handleException(ex, request, HttpStatus.NOT_FOUND);
	}

	/**
	 * Handles exceptions related to comment not found scenarios.
	 *
	 * @param ex the exception that was thrown
	 * @param request the current web request
	 * @return an ApiError object containing error details
	 */
	@ExceptionHandler(CommentNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ApiError handleCommentNotFoundException(CommentNotFoundException ex, WebRequest request) {
		return handleException(ex, request, HttpStatus.NOT_FOUND);
	}

	/**
	 * Handles exceptions related to empty file uploads.
	 *
	 * @param ex the exception that was thrown
	 * @param request the current web request
	 * @return an ApiError object containing error details
	 */
	@ExceptionHandler(EmptyFileException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiError handleEmptyFileException(EmptyFileException ex, WebRequest request) {
		return handleException(ex, request, HttpStatus.BAD_REQUEST);
	}

	/**
	 * Handles exceptions related to image upload failures.
	 *
	 * @param ex the exception that was thrown
	 * @param request the current web request
	 * @return an ApiError object containing error details
	 */
	@ExceptionHandler(ImageUploadException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ApiError handleImageUploadException(ImageUploadException ex, WebRequest request) {
		return handleException(ex, request, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * Private function that constructs an ApiError object from any given exception.
	 *
	 * @param ex the exception that was thrown
	 * @param request the current web request
	 * @return an ApiError object containing error details
	 */
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
