package com.example.blogbackend.controller;

import com.example.blogbackend.dto.BlogPostDto;
import com.example.blogbackend.dto.CommentDto;
import com.example.blogbackend.dto.CreateCommentDto;
import com.example.blogbackend.exception.ApiError;
import com.example.blogbackend.utils.SpringBootComponentTest;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class CommentControllerIntegrationTest extends SpringBootComponentTest {

    @Autowired
    MockMvc mvc;

    @WithMockUser(username = "testUser", roles = "USER")
    @Transactional
    @Test
    void when_createComment_return_blogPostWithComment() throws Exception {
        MockMultipartFile titlePart = new MockMultipartFile("title", "", "text/plain", "Test Blog Post".getBytes());
        MockMultipartFile contentPart = new MockMultipartFile("content", "", "text/plain", "This is a test blog post.".getBytes());

        MvcResult mvcResult = mvc.perform(multipart(BASE_BLOG_POST_URL)
                        .file(titlePart)
                        .file(contentPart))
                .andExpect(status().isCreated())
                .andReturn();

        BlogPostDto blogPostDto = getFromResult(mvcResult, BlogPostDto.class);
        Long blogPostId = blogPostDto.id();

        CreateCommentDto createCommentDto = new CreateCommentDto(
                "Test comment",
                blogPostId
        );

        MvcResult commentMvcResult = mvc.perform(post(BASE_COMMENTS_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createCommentDto)))
                .andExpect(status().isCreated())
                .andReturn();

        CommentDto commentDto = getFromResult(commentMvcResult, CommentDto.class);
        Assertions.assertEquals(commentDto.content(), createCommentDto.content());
    }
    
    @Test
    void when_createCommentWithInvalidBlogPostId_return_404() throws Exception {
        CreateCommentDto createCommentDto = new CreateCommentDto(
                "Test comment",
                1L
        );

        MvcResult result = mvc.perform(post(BASE_COMMENTS_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createCommentDto)))
                .andExpect(status().isNotFound())
                .andReturn();
        
        String content = result.getResponse().getContentAsString();
        ApiError apiError = objectMapper.readValue(content, ApiError.class);
        Assertions.assertEquals("Blog post with id: 1 not found.", apiError.getMessage());
        Assertions.assertEquals(404, apiError.getStatus());
        Assertions.assertEquals("Not Found", apiError.getError());
    }

    @WithMockUser(username = "testUser", roles = "USER")
    @Transactional
    @Test
    void when_getCommentById_return_comment() throws Exception {
        MockMultipartFile titlePart = new MockMultipartFile("title", "", "text/plain", "Test Blog Post".getBytes());
        MockMultipartFile contentPart = new MockMultipartFile("content", "", "text/plain", "This is a test blog post.".getBytes());

        MvcResult mvcResult = mvc.perform(multipart(BASE_BLOG_POST_URL)
                        .file(titlePart)
                        .file(contentPart))
                .andExpect(status().isCreated())
                .andReturn();

        BlogPostDto blogPostDto = getFromResult(mvcResult, BlogPostDto.class);
        Long blogPostId = blogPostDto.id();

        CreateCommentDto createCommentDto = new CreateCommentDto(
                "Test comment",
                blogPostId
        );

        MvcResult commentMvcResult = mvc.perform(post(BASE_COMMENTS_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createCommentDto)))
                .andReturn();

        CommentDto commentDto = getFromResult(commentMvcResult, CommentDto.class);

        MvcResult getCommentMvcResult = mvc.perform(get(BASE_COMMENTS_URL + "/" + commentDto.id()))
                .andExpect(status().isOk())
                .andReturn();

        CommentDto returnedComment = getFromResult(getCommentMvcResult, CommentDto.class);
        Assertions.assertEquals(commentDto.id(), returnedComment.id());
        Assertions.assertEquals(commentDto.content(), returnedComment.content());
    }
    
    @Test
    void when_getCommentByIdWithInvalidId_return_404() throws Exception {
        MvcResult result = mvc.perform(get(BASE_COMMENTS_URL + "/1"))
                .andExpect(status().isNotFound())
                .andReturn();
        
        String content = result.getResponse().getContentAsString();
        ApiError apiError = objectMapper.readValue(content, ApiError.class);
        Assertions.assertEquals("Comment with id: 1 not found.", apiError.getMessage());
        Assertions.assertEquals(404, apiError.getStatus());
        Assertions.assertEquals("Not Found", apiError.getError());
    }
}