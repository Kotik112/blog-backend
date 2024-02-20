package com.example.blogbackend.dto;

import com.example.blogbackend.domain.BlogPost;

import java.time.Instant;
import java.util.Set;

public record BlogPostDto(
        Long id, String title,
        String content, Instant createdAt,
        Instant lastEditedAt,
        Boolean isEdited,
        Set<LikeDto> likes,
        Set<CommentDto> comments){
    public BlogPost from() {
        return BlogPost.builder()
                .id(id)
                .title(title)
                .content(content)
                .build();
    }
}
