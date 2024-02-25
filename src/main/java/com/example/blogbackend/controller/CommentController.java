package com.example.blogbackend.controller;


import com.example.blogbackend.dto.CommentDto;
import com.example.blogbackend.dto.CreateCommentDto;
import com.example.blogbackend.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto createComment(@RequestBody CreateCommentDto createCommentDto) {
        return commentService.createComment(createCommentDto);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto getCommentById(@PathVariable("id") Long id) {
        return commentService.getCommentById(id);
    }
    
    @GetMapping("/blogPost/{blogPostId}")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto> getCommentsByBlogPostId(@PathVariable("blogPostId") Long blogPostId) {
        return commentService.getCommentsByBlogPostId(blogPostId);
    }
}
