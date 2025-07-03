package com.example.blogbackend.controller;

import com.example.blogbackend.dto.BlogPostDto;
import com.example.blogbackend.utils.SpringBootComponentTest;
import com.jayway.jsonpath.JsonPath;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class BlogPostControllerIntegrationTest extends SpringBootComponentTest {

    @Autowired
    MockMvc mvc;

    @WithMockUser(username = "testUser", roles = "USER")
    @Test
    void test_createBlogPostWithImageSuccess() throws Exception {
        String title = "Test title";
        String content = "Test content";

        // These must be multipart *files* to map to @RequestPart
        MockMultipartFile titlePart = new MockMultipartFile("title", "", "text/plain", title.getBytes(StandardCharsets.UTF_8));
        MockMultipartFile contentPart = new MockMultipartFile("content", "", "text/plain", content.getBytes(StandardCharsets.UTF_8));
        MockMultipartFile image = new MockMultipartFile("image", "image.jpg", "image/jpeg", "<<jpeg data>>".getBytes());

        MvcResult mvcResult = mvc.perform(multipart(BASE_BLOG_POST_URL)
                        .file(titlePart)
                        .file(contentPart)
                        .file(image))
                .andExpect(status().isCreated())
                .andReturn();

        BlogPostDto blogPostDTO = getFromResult(mvcResult, BlogPostDto.class);
        Assertions.assertEquals("Test title", blogPostDTO.title());
        Assertions.assertEquals("Test content", blogPostDTO.content());
    }

    @WithMockUser(username = "testUser", roles = "USER")
    @Test
    void when_getAllBlogPosts_then_returnBlogPosts() throws Exception {
        // Helper to create blog posts
        for (int i = 1; i <= 4; i++) {
            String title = "Test title " + i;
            String content = "Test content " + i;

            MockMultipartFile titlePart = new MockMultipartFile("title", "", "text/plain", title.getBytes(StandardCharsets.UTF_8));
            MockMultipartFile contentPart = new MockMultipartFile("content", "", "text/plain", content.getBytes(StandardCharsets.UTF_8));

            mvc.perform(multipart(BASE_BLOG_POST_URL)
                            .file(titlePart)
                            .file(contentPart)
                            .contentType(MediaType.MULTIPART_FORM_DATA))
                    .andExpect(status().isCreated());
        }

        // First page (newest posts first)
        MvcResult mvcResult1 = mvc.perform(get(BASE_BLOG_POST_URL + "?page=0&size=2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].title", is("Test title 4")))
                .andExpect(jsonPath("$.content[0].content", is("Test content 4")))
                .andExpect(jsonPath("$.content[1].title", is("Test title 3")))
                .andExpect(jsonPath("$.content[1].content", is("Test content 3")))
                .andReturn();

        // Second page (older posts)
        MvcResult mvcResult2 = mvc.perform(get(BASE_BLOG_POST_URL + "?page=1&size=2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].title", is("Test title 2")))
                .andExpect(jsonPath("$.content[0].content", is("Test content 2")))
                .andExpect(jsonPath("$.content[1].title", is("Test title 1")))
                .andExpect(jsonPath("$.content[1].content", is("Test content 1")))
                .andReturn();

        // Deserialize and assert sizes
        List<BlogPostDto> firstPage = getFromPageResult(mvcResult1, BlogPostDto.class);
        List<BlogPostDto> secondPage = getFromPageResult(mvcResult2, BlogPostDto.class);

        Assertions.assertEquals(2, firstPage.size());
        Assertions.assertEquals(2, secondPage.size());
    }


    @WithMockUser(username = "testUser", roles = "USER")
    @Transactional
    @Test
    void when_getBlogPostById_then_returnBlogPost() throws Exception {

        MockMultipartFile titlePart = new MockMultipartFile("title", "", "text/plain", "Test title".getBytes(StandardCharsets.UTF_8));
        MockMultipartFile contentPart = new MockMultipartFile("content", "", "text/plain", "Test content".getBytes(StandardCharsets.UTF_8));

        MvcResult postResult = mvc.perform(multipart(BASE_BLOG_POST_URL)
                        .file(titlePart)
                        .file(contentPart))
                .andExpect(status().isCreated())
                .andReturn();

        String response = postResult.getResponse().getContentAsString();
        String blogPostId = JsonPath.read(response, "$.id").toString();

        ResultActions resultActions = mvc.perform(get(BASE_BLOG_POST_URL + "/{id}", blogPostId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(Integer.parseInt(blogPostId))))
                .andExpect(jsonPath("$.title", is("Test title")))
                .andExpect(jsonPath("$.content", is("Test content")));

        BlogPostDto blogPostDTO = getFromResult(resultActions.andReturn(), BlogPostDto.class);
        Assertions.assertEquals("Test title", blogPostDTO.title());
        Assertions.assertEquals("Test content", blogPostDTO.content());
    }

    @Test
    void when_getBlogPostByInvalidId_then_returnNotFound() throws Exception {
        int invalidId = 999;
        mvc.perform(get(BASE_BLOG_POST_URL + "/{id}", invalidId))
                .andExpect(status().isNotFound());
    }
}
