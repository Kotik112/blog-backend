package com.example.blogbackend.controller;

import com.example.blogbackend.dto.BlogPostDto;
import com.example.blogbackend.dto.CommentDto;
import com.example.blogbackend.dto.CreateBlogPostDto;
import com.example.blogbackend.dto.CreateCommentDto;
import com.example.blogbackend.utils.SpringBootComponentTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
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
}