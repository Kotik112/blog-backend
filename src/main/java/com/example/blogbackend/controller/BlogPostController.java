package com.example.blogbackend.controller;

import com.example.blogbackend.dto.BlogPostDto;
import com.example.blogbackend.dto.CreateBlogPostDto;
import com.example.blogbackend.exception.ApiError;
import com.example.blogbackend.service.BlogPostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@SuppressWarnings("unused")
@RestController
@RequestMapping("/api/v1/blog")
@Tag(name = "Posts", description = "Operations related to blog posts")
public class BlogPostController {
    private final Logger logger = LoggerFactory.getLogger(BlogPostController.class);
    private final BlogPostService blogPostService;

    public BlogPostController(BlogPostService blogPostService) {
        this.blogPostService = blogPostService;
    }

    /**
     * Creates a new blog post with an optional image.
     *
     * @param title     the title of the blog post
     * @param content   the content of the blog post
     * @param image     the optional image file to be associated with the blog post
     * @return the created BlogPostDto
     */

    @Operation(summary = "Create a new blog post", description = "Creates a new blog post with an optional image.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Blog post created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BlogPostDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid blog post data", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PostMapping(value = "", consumes = "multipart/form-data", produces = "application/json")
    public ResponseEntity<BlogPostDto> createBlogPost(
            @RequestPart("title") String title,
            @RequestPart("content") String content,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        CreateBlogPostDto dto = new CreateBlogPostDto(title, content);
        BlogPostDto createdPost = blogPostService.createBlogPost(dto, image);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPost);
    }

    /**
     * Retrieves a paginated list of all blog posts.
     *
     * @param page the page number to retrieve (default is 0)
     * @param size the number of blog posts per page (default is 5)
     * @return a Page containing BlogPostDto objects
     */

    @Operation(summary = "Get all blog posts", description = "Retrieves a paginated list of all blog posts.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved blog posts",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = BlogPostDto.class)))),
            @ApiResponse(responseCode = "400", description = "Invalid pagination parameters", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "")
    public Page<BlogPostDto> getAllBlogPosts(@RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "5") int size ){
        return blogPostService.getAllBlogPosts(page, size);
    }

    /**
     * Retrieves a blog post by its ID.
     *
     * @param id the ID of the blog post to retrieve
     * @return the BlogPostDto corresponding to the given ID
     */

    @Operation(summary = "Get a blog post by ID", description = "Retrieves a blog post by its unique identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Blog post found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BlogPostDto.class))),
            @ApiResponse(responseCode = "404", description = "Blog post not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class)))
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/{id}")
    public BlogPostDto getBlogPostById(@PathVariable("id") Long id) {
        return blogPostService.getBlogPostById(id);
    }
}
