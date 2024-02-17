package com.example.blogbackend.dto;

import com.example.blogbackend.domain.BlogPost;

import java.time.Instant;

public record CreateBlogPostDto(String title, String content) {
    public BlogPost toDomain() {
        return BlogPost.builder()
                .title(title)
                .content(content)
                .isEdited(false)
                .createdAt(Instant.now())
                .build();
    }
}
