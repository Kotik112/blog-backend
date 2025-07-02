package com.example.blogbackend.controller;


import com.example.blogbackend.dto.BlogPostDto;
import com.example.blogbackend.dto.CreateBlogPostDto;
import com.example.blogbackend.utils.SpringBootComponentTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
public class BlogPostControllerIntegrationTest extends SpringBootComponentTest {
    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper mapper;

    @Test
    public void when_createBlogPost_then_returnBlogPost() throws Exception {
        String title = "Test title";
        String content = "Test content";
        
        MockMultipartFile image = new MockMultipartFile("image", "image.jpg", "image/jpeg", "<<jpeg data>>".getBytes());
        
        // Convert title and content to MockMultipartFile as form data
        MockMultipartFile titlePart = new MockMultipartFile("title", "", "text/plain", title.getBytes(StandardCharsets.UTF_8));
        MockMultipartFile contentPart = new MockMultipartFile("content", "", "text/plain", content.getBytes(StandardCharsets.UTF_8));
        
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.multipart(BASE_BLOG_POST_URL)
                                                  .file(titlePart)
                                                  .file(contentPart)
                                                  .file(image))
                                      .andExpect(status().isCreated())
                                      .andReturn();
        
        BlogPostDto blogPostDTO = getFromResult(mvcResult, BlogPostDto.class);
        assertEquals("Test title", blogPostDTO.title());
        assertEquals("Test content", blogPostDTO.content());
    }
    
    @Test
    public void when_getAllBlogPosts_then_returnBlogPosts() throws Exception {
        // BlogPostDto objects for testing
        CreateBlogPostDto blogPost1 = new CreateBlogPostDto("Test title 1", "Test content 1");
        CreateBlogPostDto blogPost2 = new CreateBlogPostDto("Test title 2", "Test content 2");
        CreateBlogPostDto blogPost3 = new CreateBlogPostDto("Test title 3", "Test content 3");
        CreateBlogPostDto blogPost4 = new CreateBlogPostDto("Test title 4", "Test content 4");
        
        // Create the blog posts
        mvc.perform(post(BASE_BLOG_POST_URL)
                            .contentType(APPLICATION_JSON)
                            .content(mapper.writeValueAsString(blogPost1)))
                .andExpect(status().isCreated());
        
        mvc.perform(post(BASE_BLOG_POST_URL)
                            .contentType(APPLICATION_JSON)
                            .content(mapper.writeValueAsString(blogPost2)))
                .andExpect(status().isCreated());
        
        mvc.perform(post(BASE_BLOG_POST_URL)
                            .contentType(APPLICATION_JSON)
                            .content(mapper.writeValueAsString(blogPost3)))
                .andExpect(status().isCreated());
        
        mvc.perform(post(BASE_BLOG_POST_URL)
                            .contentType(APPLICATION_JSON)
                            .content(mapper.writeValueAsString(blogPost4)))
                .andExpect(status().isCreated());
        
        // Fetch the first page of blog posts
        MvcResult mvcResult1 = mvc.perform(get(BASE_BLOG_POST_URL + "?page=0&size=2"))
                                      .andExpect(status().isOk())
                                      .andExpect(jsonPath("$.content", hasSize(2)))
                                      .andExpect(jsonPath("$.content[0].title", is("Test title 1")))
                                      .andExpect(jsonPath("$.content[0].content", is("Test content 1")))
                                      .andExpect(jsonPath("$.content[1].title", is("Test title 2")))
                                      .andExpect(jsonPath("$.content[1].content", is("Test content 2")))
                                      .andReturn();
        
        // Verify that there is a next page
        MvcResult mvcResult2 = mvc.perform(get(BASE_BLOG_POST_URL + "?page=1&size=2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].title", is("Test title 3")))
                .andExpect(jsonPath("$.content[0].content", is("Test content 3")))
                .andExpect(jsonPath("$.content[1].title", is("Test title 4")))
                .andExpect(jsonPath("$.content[1].content", is("Test content 4")))
                                       .andReturn();
        
        // Deserialize the response of the first page
        List<BlogPostDto> firstPage = getFromPageResult(mvcResult1, BlogPostDto.class);
        List<BlogPostDto> secondPage = getFromPageResult(mvcResult2, BlogPostDto.class);
        assertEquals(2, firstPage.size());
        assertEquals(2, secondPage.size());
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
