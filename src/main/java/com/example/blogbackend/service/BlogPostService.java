package com.example.blogbackend.service;


import com.example.blogbackend.dto.BlogPostDto;
import com.example.blogbackend.dto.CreateBlogPostDto;
import com.example.blogbackend.domain.BlogPost;
import com.example.blogbackend.exception.BlogPostNotFoundException;
import com.example.blogbackend.repository.BlogPostRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BlogPostService {

    private final BlogPostRepository blogPostRepository;

    public BlogPostService(BlogPostRepository blogPostRepository) {
        this.blogPostRepository = blogPostRepository;
    }

    public BlogPostDto createBlogPost(CreateBlogPostDto blogPostDTO) {
        BlogPost blogPost = BlogPost.builder()
                .title(blogPostDTO.title())
                .content(blogPostDTO.content())
                .createdAt(Instant.now())
                .isEdited(false)
                .build();
        BlogPost newBlogPost = blogPostRepository.save(blogPost);
        return BlogPost.from(newBlogPost);
    }

    public List<BlogPostDto> getAllBlogPosts() {
        List<BlogPost> blogPostList = blogPostRepository.findAll();
        return blogPostList.stream().map(BlogPost::from).collect(Collectors.toList());
    }

    public BlogPostDto getBlogPostById(Long id) {
        BlogPost blogPost = blogPostRepository.findById(id).orElseThrow(
                () -> new BlogPostNotFoundException("Blog post with id " + id + " not found.")
        );
        return BlogPost.from(blogPost);
    }
}
