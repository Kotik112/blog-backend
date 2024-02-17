package com.example.blogbackend.domain;


import com.example.blogbackend.dto.CommentDto;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "comment")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String content;
    private Instant createdAt;
    private Instant lastEditedAt;
    private Boolean isEdited;
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
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Comment comment = (Comment) o;

        return getContent().equals(comment.getContent());
    }

    @Override
    public int hashCode() {
        return getContent().hashCode();
    }
}
