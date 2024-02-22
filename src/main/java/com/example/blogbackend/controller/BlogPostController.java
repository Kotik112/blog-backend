package com.example.blogbackend.controller;

import com.example.blogbackend.dto.BlogPostDto;
import com.example.blogbackend.dto.CreateBlogPostDto;
import com.example.blogbackend.service.BlogPostService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/blog")
public class BlogPostController {

    private final BlogPostService blogPostService;

    public BlogPostController(BlogPostService blogPostService) {
        this.blogPostService = blogPostService;
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public BlogPostDto createBlogPost(@RequestBody CreateBlogPostDto blogPostDTO) {
        return blogPostService.createBlogPost(blogPostDTO);
    }

    @GetMapping("")
    public Page<BlogPostDto> getAllBlogPosts(@RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "5") int size ){
        return blogPostService.getAllBlogPosts(page, size);
    }

    @GetMapping("/{id}")
    public BlogPostDto getBlogPostById(@PathVariable("id") Long id) {
        return blogPostService.getBlogPostById(id);
    }
}
