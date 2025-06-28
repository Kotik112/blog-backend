package com.example.blogbackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Data Transfer Object for creating a new comment")
public record CreateCommentDto(
        @Schema(description = "Content of the comment", example = "This is a great post!")
        String content,
        @Schema(description = "ID of the blog post to which this comment belongs", example = "101")
        Long blogPostId
) {
    public CommentDto toDomain() {
        return CommentDto.builder()
                .content(content)
                .isEdited(false)
                .build();
    }
}
