package com.example.blogbackend.dto;

import com.example.blogbackend.domain.BlogPost;
import com.example.blogbackend.domain.Comment;
import com.example.blogbackend.domain.Like;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Schema(description = "Data Transfer Object for a blog post")
public record BlogPostDto(
    @Schema(description = "Unique ID of the blog post", example = "101") Long id,
    @Schema(description = "Title of the blog post", example = "My First Spring Boot Post")
        String title,
    @Schema(
            description = "Content of the blog post",
            example = "This is the full content of my post...")
        String content,
    @Schema(description = "Creation timestamp", example = "2025-06-26T03:15:19.293Z")
        Instant createdAt,
    @Schema(description = "Last edited timestamp", example = "2025-06-26T03:20:00.000Z")
        Instant lastEditedAt,
    @Schema(description = "Indicates whether the post has been edited", example = "true")
        Boolean isEdited,
    @Schema(description = "List of likes associated with this post") Set<LikeDto> likes,
    @Schema(description = "List of comments on the blog post") Set<CommentDto> comments,
    @Schema(description = "Image metadata if an image is attached") ImageDto image) {

  public static BlogPostDto toDto(BlogPost post) {
    Set<LikeDto> likeDtos =
        post.getLikes() != null
            ? post.getLikes().stream().map(Like::toDTO).collect(Collectors.toSet())
            : Collections.emptySet();

    Set<CommentDto> commentDtos =
        post.getComments() != null
            ? post.getComments().stream().map(Comment::toDTO).collect(Collectors.toSet())
            : Collections.emptySet();

    ImageDto imageDto = post.getImage() != null ? ImageDto.toDto(post.getImage()) : null;

    return new BlogPostDto(
        post.getId(),
        post.getTitle(),
        post.getContent(),
        post.getCreatedAt(),
        post.getLastEditedAt(),
        post.getIsEdited(),
        likeDtos,
        commentDtos,
        imageDto);
  }
}
