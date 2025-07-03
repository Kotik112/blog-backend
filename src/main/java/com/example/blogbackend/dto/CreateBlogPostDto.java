package com.example.blogbackend.dto;

import com.example.blogbackend.domain.BlogPost;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@Schema(description = "Data Transfer Object for creating a new blog post")
public class CreateBlogPostDto {

  private String title;
  private String content;

  public CreateBlogPostDto(String title, String content) {
    this.title = title;
    this.content = content;
  }

  public BlogPost toDomain() {
    BlogPost blogPost = new BlogPost();
    blogPost.setTitle(this.title);
    blogPost.setContent(this.content);
    blogPost.setCreatedAt(Instant.now());
    blogPost.setLastEditedAt(Instant.now());
    blogPost.setIsEdited(false);
    return blogPost;
  }
}
