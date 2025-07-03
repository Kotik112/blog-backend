package com.example.blogbackend.repository;

import com.example.blogbackend.domain.Comment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CommentRepository extends JpaRepository<Comment, Long> {
  @Query("SELECT c FROM Comment c WHERE c.blogPost.id = :blogPostId")
  List<Comment> findByBlogPostId(Long blogPostId);
}
