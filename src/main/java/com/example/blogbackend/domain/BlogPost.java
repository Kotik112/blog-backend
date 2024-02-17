package com.example.blogbackend.domain;


import com.example.blogbackend.dto.BlogPostDto;
import com.example.blogbackend.dto.CommentDto;
import com.example.blogbackend.dto.LikeDto;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "blog_post")
public class BlogPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String content;
    private Instant createdAt;
    private Instant lastEditedAt = null;
    private Boolean isEdited = false;

    @OneToMany(mappedBy = "blogPost", cascade = CascadeType.ALL)
    private Set<Comment> comments = new HashSet<>();

    @OneToMany(mappedBy = "blogPost", cascade = CascadeType.ALL)
    private Set<Like> likes = new HashSet<>();

    public static BlogPostDto from(BlogPost post) {
        Set<LikeDto> likeDtos = post.getLikes() != null
                ? post.getLikes().stream().map(Like::toDTO).collect(Collectors.toSet())
                : Collections.emptySet();

        Set<CommentDto> commentDtos = post.getComments() != null
                ? post.getComments().stream().map(Comment::toDTO).collect(Collectors.toSet())
                : Collections.emptySet();

        return new BlogPostDto(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getCreatedAt(),
                post.getLastEditedAt(),
                post.getIsEdited(), likeDtos, commentDtos
        );
    }

}
