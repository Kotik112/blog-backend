package com.example.blogbackend.domain;

import com.example.blogbackend.dto.LikeDto;
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
@Table(name = "\"like\"")
public class Like {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Instant createdAt;

  @ManyToOne
  @JoinColumn(name = "blogPostId")
  private BlogPost blogPost;

  public LikeDto toDTO() {
    return new LikeDto(this.createdAt, this.blogPost.getId());
  }
}
