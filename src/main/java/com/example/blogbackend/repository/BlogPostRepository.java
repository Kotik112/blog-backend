package com.example.blogbackend.repository;

import com.example.blogbackend.domain.BlogPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlogPostRepository extends JpaRepository<BlogPost, Long> {
  Page<BlogPost> findAllByCreatedBy_Username(String username, Pageable pageable);
}
