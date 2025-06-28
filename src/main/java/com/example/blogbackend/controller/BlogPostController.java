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
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@SuppressWarnings("unused")
@RestController
@RequestMapping("/api/v1/blog")
@Tag(name = "Posts", description = "Operations related to blog posts")
public class BlogPostController {

    private final BlogPostService blogPostService;

    public BlogPostController(BlogPostService blogPostService) {
        this.blogPostService = blogPostService;
    }

    /**
     * Creates a new blog post with an optional image.
     *
     * @param blogPostDTO the DTO containing the details of the blog post to be created
     * @param image       the optional image file to be associated with the blog post
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
    public ResponseEntity<?> createBlogPost(@ModelAttribute CreateBlogPostDto blogPostDTO,
                                            @RequestParam(value = "image", required = false) MultipartFile image) {
        System.out.println("Title: " + blogPostDTO.getTitle());
        System.out.println("Content: " + blogPostDTO.getContent());
        BlogPostDto createdPost = blogPostService.createBlogPost(blogPostDTO, image);
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
