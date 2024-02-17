package com.example.blogbackend.dto;

import java.time.Instant;

public record LikeDto(Instant createdAt, Long blogPostId) {
}
