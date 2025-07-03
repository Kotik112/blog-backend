package com.example.blogbackend.dto;

import com.example.blogbackend.domain.Comment;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import lombok.Builder;

@Schema(description = "Represents a comment on a blog post")
@Builder
public record CommentDto(
    @Schema(description = "Unique ID of the comment", example = "501") Long id,
    @Schema(description = "Content of the comment", example = "Great article!") String content,
    @Schema(
            description = "Timestamp when the comment was created",
            example = "2025-06-26T03:15:19.293Z")
        Instant createdAt,
    @Schema(
            description = "Timestamp when the comment was last edited",
            example = "2025-06-26T03:20:00.000Z")
        Instant lastEditedAt,
    @Schema(description = "Whether the comment has been edited", example = "true")
        Boolean isEdited) {
  public static CommentDto from(Comment comment) {
    return CommentDto.builder()
        .id(comment.getId())
        .content(comment.getContent())
        .createdAt(comment.getCreatedAt())
        .lastEditedAt(comment.getLastEditedAt())
        .isEdited(comment.getIsEdited())
        .build();
  }
}
