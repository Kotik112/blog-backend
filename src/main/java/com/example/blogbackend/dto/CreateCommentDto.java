package com.example.blogbackend.dto;

public record CreateCommentDto(String content, Long blogPostId) {
    public CommentDto toDomain() {
        return CommentDto.builder()
                .content(content)
                .isEdited(false)
                .build();
    }
}
