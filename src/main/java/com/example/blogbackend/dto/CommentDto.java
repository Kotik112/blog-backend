package com.example.blogbackend.dto;

import com.example.blogbackend.domain.Comment;
import lombok.Builder;

import java.time.Instant;

@Builder
public record CommentDto(
        Long id,
        String content,
        Instant createdAt,
        Instant lastEditedAt,
        Boolean isEdited
) {
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
