package com.example.blogbackend.exception;

import com.example.blogbackend.provider.TimeProvider;
import java.time.LocalDateTime;
import java.util.Objects;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

/**
 * Global exception handler for the blog backend application. Handles specific exceptions and
 * returns appropriate error responses.
 */
@SuppressWarnings("unused")
@RestControllerAdvice
public class GlobalExceptionHandler {

  private final TimeProvider timeProvider;

  public GlobalExceptionHandler(TimeProvider timeProvider) {
    this.timeProvider = timeProvider;
  }

  /**
   * Handles all exceptions that are not explicitly handled by other methods.
   *
   * @param ex the exception that was thrown
   * @param request the current web request
   * @return an ApiError object containing error details
   */
  @ExceptionHandler(BlogPostNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ApiError handleBlogPostNotFoundException(
      BlogPostNotFoundException ex, WebRequest request) {
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
   * Handles exceptions related to image not found scenarios.
   *
   * @param ex the exception that was thrown
   * @param request the current web request
   * @return an ApiError object containing error details
   */
  @ExceptionHandler(ImageNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ApiError handleImageNotFoundException(ImageNotFoundException ex, WebRequest request) {
    return handleException(ex, request, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(RegistrationFailureException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ApiError handleRegistrationFailureException(
      RegistrationFailureException ex, WebRequest request) {
    return handleException(ex, request, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(LoginFailureException.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public ApiError handleLoginFailureException(LoginFailureException ex, WebRequest request) {
    return handleException(ex, request, HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(UserAlreadyExistsException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  public ApiError handleUserAlreadyExistsException(
      UserAlreadyExistsException ex, WebRequest request) {
    return handleException(ex, request, HttpStatus.CONFLICT);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ApiError handleValidationException(
      MethodArgumentNotValidException ex, WebRequest request) {
    // Extract the first non-null error message or use fallback
    String errorMessage =
        ex.getBindingResult().getFieldErrors().stream()
            .map(DefaultMessageSourceResolvable::getDefaultMessage)
            .filter(Objects::nonNull)
            .findFirst()
            .orElse("Validation failed");

    return buildApiError(HttpStatus.BAD_REQUEST, errorMessage, request);
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

  private ApiError buildApiError(HttpStatus status, String message, WebRequest request) {
    ApiError apiError = new ApiError();
    apiError.setTimestamp(LocalDateTime.now());
    apiError.setStatus(status.value());
    apiError.setError(status.getReasonPhrase());
    apiError.setMessage(message);
    apiError.setPath(request.getDescription(false));
    return apiError;
  }
}
