package com.example.blogbackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "Data Transfer Object for a like on a blog post")
public record LikeDto(
        @Schema(description = "Timestamp when the like was created", example = "2025-06-26T03:15:19.293Z")
        Instant createdAt,
        @Schema(description = "ID of the blog post that was liked", example = "101")
        Long blogPostId ) {}
