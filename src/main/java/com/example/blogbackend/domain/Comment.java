package com.example.blogbackend.domain;

import com.example.blogbackend.dto.CommentDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "comment")
@Schema(description = "Represents a comment on a blog post")
public class Comment {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Schema(description = "Unique identifier for the comment", example = "501")
  private Long id;

  @Schema(description = "Content of the comment", example = "Great article!")
  private String content;

  @Schema(
      description = "Timestamp when the comment was created",
      example = "2025-06-26T03:15:19.293Z")
  private Instant createdAt;

  @Schema(
      description = "Timestamp when the comment was last edited",
      example = "2025-06-26T03:20:00.000Z")
  private Instant lastEditedAt;

  @Schema(description = "Whether the comment has been edited", example = "false")
  private Boolean isEdited;

  @Schema(description = "The blog post to which this comment belongs")
  @ManyToOne
  @JoinColumn(name = "blogPostId")
  private BlogPost blogPost;

  public static Comment from(CommentDto comment) {
    return Comment.builder()
        .id(comment.id())
        .content(comment.content())
        .createdAt(comment.createdAt())
        .lastEditedAt(comment.lastEditedAt())
        .isEdited(comment.isEdited())
        .build();
  }

  public CommentDto toDTO() {
    return CommentDto.builder()
        .id(this.id)
        .content(this.content)
        .createdAt(this.createdAt)
        .lastEditedAt(this.lastEditedAt)
        .isEdited(this.isEdited)
        .build();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Comment comment = (Comment) o;

    return getContent().equals(comment.getContent());
  }

  @Override
  public int hashCode() {
    return getContent().hashCode();
  }
}
