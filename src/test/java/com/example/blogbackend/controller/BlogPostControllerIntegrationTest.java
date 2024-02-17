package com.example.blogbackend.controller;


import com.example.blogbackend.dto.BlogPostDto;
import com.example.blogbackend.dto.CreateBlogPostDto;
import com.example.blogbackend.utils.SpringBootComponentTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
public class BlogPostControllerIntegrationTest extends SpringBootComponentTest {
    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper mapper;

    @Test
    public void when_createBlogPost_then_returnBlogPost() throws Exception {
        CreateBlogPostDto createdBlogPost = new CreateBlogPostDto("Test title", "Test content");
        ResultActions resultActions = mvc.perform(post(BASE_BLOG_POST_URL)
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(createdBlogPost)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.comments", is(List.of())))
                .andExpect(jsonPath("$.isEdited", is(false)));

        BlogPostDto blogPostDTO = getFromResult(resultActions.andReturn(), BlogPostDto.class);
        assertEquals("Test title", blogPostDTO.title());
        assertEquals("Test content", blogPostDTO.content());
    }

    @Test
    public void when_getAllBlogPosts_then_returnBlogPosts() throws Exception {
        CreateBlogPostDto blogPost1 = new CreateBlogPostDto("Test title", "Test content");
        mvc.perform(post(BASE_BLOG_POST_URL)
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(blogPost1)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        CreateBlogPostDto blogPost2 = new CreateBlogPostDto("Test title 2", "Test content 2");
        mvc.perform(post(BASE_BLOG_POST_URL)
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(blogPost2)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ResultActions resultActions = mvc.perform(get(BASE_BLOG_POST_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(2)))
                .andExpect(jsonPath("$[0].title", is("Test title")))
                .andExpect(jsonPath("$[0].content", is("Test content")))
                .andExpect(jsonPath("$[1].title", is("Test title 2")))
                .andExpect(jsonPath("$[1].content", is("Test content 2")));

        List<BlogPostDto> blogPostDtos = getFromListResult(resultActions.andReturn(), BlogPostDto.class);
        assertEquals(2, blogPostDtos.size());
    }

    @Test
    public void when_getBlogPostById_then_returnBlogPost() throws Exception {
        CreateBlogPostDto blogPost1 = new CreateBlogPostDto("Test title", "Test content");
        MvcResult postResult = mvc.perform(post(BASE_BLOG_POST_URL)
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(blogPost1)))
                .andExpect(status().isCreated())
                .andReturn();

        String response = postResult.getResponse().getContentAsString();
        System.out.println("Result = " + response);
        String blogPostId = JsonPath.read(response, "$.id").toString();

        ResultActions resultActions = mvc.perform(get(BASE_BLOG_POST_URL + "/{id}", blogPostId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(Integer.parseInt(blogPostId))))
                .andExpect(jsonPath("$.title", is("Test title")))
                .andExpect(jsonPath("$.content", is("Test content")));

        BlogPostDto blogPostDTO = getFromResult(resultActions.andReturn(), BlogPostDto.class);
        assertEquals("Test title", blogPostDTO.title());
        assertEquals("Test content", blogPostDTO.content());
    }

    //is it better to test exception in Unit tests?
    @Test
    public void when_getBlogPostByInvalidId_then_returnNotFound() throws Exception {
        int invalidId = 999;
        mvc.perform(get(BASE_BLOG_POST_URL + "/{id}", invalidId))
                .andExpect(status().isNotFound());
    }
}
