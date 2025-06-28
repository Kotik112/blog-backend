package com.example.blogbackend.controller;


import com.example.blogbackend.dto.CommentDto;
import com.example.blogbackend.dto.CreateCommentDto;
import com.example.blogbackend.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@SuppressWarnings("unused")
@Tag(name = "Comments", description = "Operations related to Blog post comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    /**
     * Creates a new comment.
     *
     * @param createCommentDto the DTO containing the details of the comment to be created
     * @return the created CommentDto
     */

    @Operation(summary = "Create a new comment", description = "Creates a new comment for a blog post.")
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto createComment(@RequestBody CreateCommentDto createCommentDto) {
        return commentService.createComment(createCommentDto);
    }

    /**
     * Retrieves a comment by its ID.
     *
     * @param id the ID of the comment to retrieve
     * @return the CommentDto corresponding to the given ID
     */

    @Operation(summary = "Get a comment by ID", description = "Retrieves a comment by its unique identifier.")
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto getCommentById(@PathVariable("id") Long id) {
        return commentService.getCommentById(id);
    }

    /**
     * Retrieves all comments associated with a specific blog post.
     *
     * @param blogPostId the ID of the blog post for which to retrieve comments
     * @return a list of CommentDto objects associated with the specified blog post
     */

    @Operation(summary = "Get comments by blog post ID", description = "Retrieves all comments associated with a specific blog post.")
    @GetMapping("/blogPost/{blogPostId}")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto> getCommentsByBlogPostId(@PathVariable("blogPostId") Long blogPostId) {
        return commentService.getCommentsByBlogPostId(blogPostId);
    }
}
