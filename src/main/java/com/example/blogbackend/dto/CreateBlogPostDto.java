package com.example.blogbackend.dto;

import com.example.blogbackend.domain.BlogPost;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Setter
@Getter
@Schema(description = "Data Transfer Object for creating a new blog post")
public class CreateBlogPostDto {

    private String title;
    private String content;

    public CreateBlogPostDto(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public BlogPost toDomain() {
        return BlogPost.builder()
                .title(title)
                .content(content)
                .isEdited(false)
                .createdAt(Instant.now())
                .build();
    }
}
