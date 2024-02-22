package com.example.blogbackend.service;


import com.example.blogbackend.domain.BlogPost;
import com.example.blogbackend.dto.BlogPostDto;
import com.example.blogbackend.dto.CreateBlogPostDto;
import com.example.blogbackend.exception.BlogPostNotFoundException;
import com.example.blogbackend.repository.BlogPostRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BlogPostService {

    private final BlogPostRepository blogPostRepository;

    public BlogPostService(BlogPostRepository blogPostRepository) {
        this.blogPostRepository = blogPostRepository;
    }

    public BlogPostDto createBlogPost(CreateBlogPostDto blogPostDTO) {
        BlogPost blogPost = blogPostDTO.toDomain();
        BlogPost newBlogPost = blogPostRepository.save(blogPost);
        return BlogPost.toDto(newBlogPost);
    }

    public Page<BlogPostDto> getAllBlogPosts(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<BlogPost> blogPostList = blogPostRepository.findAll(pageRequest);
        
        List<BlogPostDto> blogPostDtoList = blogPostList.getContent().stream()
                .map(BlogPost::toDto)
                .toList();
        
        return blogPostList.map(BlogPost::toDto);
    }

    public BlogPostDto getBlogPostById(Long id) {
        BlogPost blogPost = blogPostRepository.findById(id).orElseThrow(
                () -> new BlogPostNotFoundException("Blog post with id: " + id + " not found.")
        );
        return BlogPost.toDto(blogPost);
    }
}
