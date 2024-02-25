package com.example.blogbackend.dto;

import com.example.blogbackend.domain.BlogPost;
import com.example.blogbackend.domain.Comment;
import com.example.blogbackend.domain.Like;

import java.time.Instant;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public record BlogPostDto(
        Long id,
        String title,
        String content,
        Instant createdAt,
        Instant lastEditedAt,
        Boolean isEdited,
        Set<LikeDto> likes,
        Set<CommentDto> comments,
        ImageDto image
) {
    public BlogPost toDomain() {
        return BlogPost.builder()
                .id(id)
                .title(title)
                .content(content)
                .build();
    }
    
    public static BlogPostDto toDto(BlogPost post) {
        Set<LikeDto> likeDtos = post.getLikes() != null
                                        ? post.getLikes().stream().map(Like::toDTO).collect(Collectors.toSet())
                                        : Collections.emptySet();
        
        Set<CommentDto> commentDtos = post.getComments() != null
                                              ? post.getComments().stream().map(Comment::toDTO).collect(Collectors.toSet())
                                              : Collections.emptySet();
        
        ImageDto imageDto = post.getImage() != null
                                    ? ImageDto.toDto(post.getImage())
                                    : null;
        
        // likes are not being added to the dto yet WIP!
        return new BlogPostDto(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getCreatedAt(),
                post.getLastEditedAt(),
                post.getIsEdited(),
                likeDtos,
                commentDtos,
                imageDto
        
        );
    }
}
