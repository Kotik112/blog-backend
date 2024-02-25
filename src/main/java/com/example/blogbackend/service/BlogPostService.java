package com.example.blogbackend.service;


import com.example.blogbackend.domain.BlogPost;
import com.example.blogbackend.dto.BlogPostDto;
import com.example.blogbackend.dto.CreateBlogPostDto;
import com.example.blogbackend.exception.BlogPostNotFoundException;
import com.example.blogbackend.repository.BlogPostRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
        return BlogPostDto.toDto(newBlogPost);
    }

    public Page<BlogPostDto> getAllBlogPosts(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<BlogPost> blogPostPage = blogPostRepository.findAll(pageRequest);
        
        List<BlogPostDto> blogPostDtoList = blogPostPage.getContent().stream()
                .map(BlogPostDto::toDto)
                .collect(Collectors.toList());
        
        System.out.println(new PageImpl<>(blogPostDtoList, pageRequest, blogPostPage.getTotalPages()));
        return new PageImpl<>(blogPostDtoList, pageRequest, blogPostPage.getTotalPages());
    }

    public BlogPostDto getBlogPostById(Long id) {
        BlogPost blogPost = blogPostRepository.findById(id).orElseThrow(
                () -> new BlogPostNotFoundException("Blog post with id: " + id + " not found.")
        );
        return BlogPostDto.toDto(blogPost);
    }
}
