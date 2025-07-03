package com.example.blogbackend.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Represents an error response.")
public class ApiError {
  @Schema(description = "Represents an error response.")
  private LocalDateTime timestamp;

  @Schema(description = "HTTP status code of the error.", example = "404")
  private int status;

  @Schema(description = "Error reason phrase.", example = "Not Found")
  private String error;

  @Schema(description = "Detailed error message.", example = "Blog post with id: 1 not found.")
  private String message;

  @Schema(description = "Path of the request that caused the error.", example = "/api/v1/blog/1")
  private String path;
}
