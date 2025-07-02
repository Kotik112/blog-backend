package com.example.blogbackend.controller;

import com.example.blogbackend.dto.BlogPostDto;
import com.example.blogbackend.dto.CommentDto;
import com.example.blogbackend.dto.CreateBlogPostDto;
import com.example.blogbackend.dto.CreateCommentDto;
import com.example.blogbackend.exception.ApiError;
import com.example.blogbackend.utils.SpringBootComponentTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
public class CommentControllerIntegrationTest extends SpringBootComponentTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper mapper;

    @Test
    public void when_createComment_return_blogPostWithComment() throws Exception {

        CreateBlogPostDto createBlogPostDto = new CreateBlogPostDto(
                "Test title",
                "Test content"
        );

        MvcResult mvcResult = mvc.perform(post(BASE_BLOG_POST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(createBlogPostDto)))
                .andReturn();

        BlogPostDto blogPostDto = getFromResult(mvcResult, BlogPostDto.class);
        Long blogPostId = blogPostDto.id();

        CreateCommentDto createCommentDto = new CreateCommentDto(
                "Test comment",
                blogPostId
        );

        MvcResult commentMvcResult = mvc.perform(post(BASE_COMMENTS_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(createCommentDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andReturn();

        CommentDto commentDto = getFromResult(commentMvcResult, CommentDto.class);
        assertEquals(commentDto.content(), createCommentDto.content());
    }
    
    @Test
    public void when_createCommentWithInvalidBlogPostId_return_404() throws Exception {
        CreateCommentDto createCommentDto = new CreateCommentDto(
                "Test comment",
                1L
        );

        MvcResult result = mvc.perform(post(BASE_COMMENTS_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(createCommentDto)))
                .andExpect(status().isNotFound())
                .andReturn();
        
        String content = result.getResponse().getContentAsString();
        ApiError apiError = mapper.readValue(content, ApiError.class);
        assertEquals("Blog post with id: 1 not found.", apiError.getMessage());
        assertEquals(404, apiError.getStatus());
        assertEquals("Not Found", apiError.getError());
    }
    
    @Test
    public void when_getCommentById_return_comment() throws Exception {
        CreateBlogPostDto createBlogPostDto = new CreateBlogPostDto(
                "Test title",
                "Test content"
        );

        MvcResult mvcResult = mvc.perform(post(BASE_BLOG_POST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(createBlogPostDto)))
                .andReturn();

        BlogPostDto blogPostDto = getFromResult(mvcResult, BlogPostDto.class);
        Long blogPostId = blogPostDto.id();

        CreateCommentDto createCommentDto = new CreateCommentDto(
                "Test comment",
                blogPostId
        );

        MvcResult commentMvcResult = mvc.perform(post(BASE_COMMENTS_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(createCommentDto)))
                .andReturn();

        CommentDto commentDto = getFromResult(commentMvcResult, CommentDto.class);

        MvcResult getCommentMvcResult = mvc.perform(get(BASE_COMMENTS_URL + "/" + commentDto.id()))
                .andExpect(status().isOk())
                .andReturn();

        CommentDto returnedComment = getFromResult(getCommentMvcResult, CommentDto.class);
        assertEquals(commentDto.id(), returnedComment.id());
        assertEquals(commentDto.content(), returnedComment.content());
    }
    
    @Test
    public void when_getCommentByIdWithInvalidId_return_404() throws Exception {
        MvcResult result = mvc.perform(get(BASE_COMMENTS_URL + "/1"))
                .andExpect(status().isNotFound())
                .andReturn();
        
        String content = result.getResponse().getContentAsString();
        ApiError apiError = mapper.readValue(content, ApiError.class);
        assertEquals("Comment with id: 1 not found.", apiError.getMessage());
        assertEquals(404, apiError.getStatus());
        assertEquals("Not Found", apiError.getError());
    }
}