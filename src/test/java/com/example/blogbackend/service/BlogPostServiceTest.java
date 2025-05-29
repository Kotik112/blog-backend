package com.example.blogbackend.service;

import com.example.blogbackend.domain.BlogPost;
import com.example.blogbackend.dto.BlogPostDto;
import com.example.blogbackend.dto.CreateBlogPostDto;
import com.example.blogbackend.exception.BlogPostNotFoundException;
import com.example.blogbackend.provider.TimeProvider;
import com.example.blogbackend.repository.BlogPostRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.Instant;
import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class BlogPostServiceTest {

    @InjectMocks
    BlogPostService blogPostService;
    @Mock
    BlogPostRepository blogPostRepository;

    @Mock
    TimeProvider timeProvider;

    @Test
    public void test_createBlogPost() {
        // Create the input data
        CreateBlogPostDto createBlogPostDTO = new CreateBlogPostDto("Test title", "Test content");

        // Expected output data
        BlogPostDto expectedBlogPostDto = new BlogPostDto(1L, "Test title", "Test content", Instant.parse("2023-04-04T12:42:00Z"), null, false, Set.of(), Set.of(), null);

        BlogPost blogPost = new BlogPost();
        blogPost.setId(1L);
        blogPost.setTitle("Test title");
        blogPost.setContent("Test content");
        blogPost.setCreatedAt(Instant.parse("2023-04-04T12:42:00Z"));
        blogPost.setIsEdited(false);

        when(blogPostRepository.save(any())).thenReturn(blogPost);
        BlogPostDto blogPostResult = blogPostService.createBlogPost(createBlogPostDTO, null);
        verify(blogPostRepository, times(1)).save(any());

        assertEquals(expectedBlogPostDto, blogPostResult);
    }
    
    @Test
    public void when_getAllBlogPosts_then_returnBlogPosts() {
        // Expected output data
        BlogPostDto expectedBlogPostDto = new BlogPostDto(1L,
                                                          "Test title",
                                                          "Test content",
                                                          Instant.parse("2023-04-04T12:42:00Z"),
                                                          null,
                                                          false,
                                                          Collections.emptySet(),
                                                          Collections.emptySet(),
                                                          null);
        
        BlogPost blogPost = new BlogPost();
        blogPost.setId(1L);
        blogPost.setTitle("Test title");
        blogPost.setContent("Test content");
        blogPost.setCreatedAt(Instant.parse("2023-04-04T12:42:00Z"));
        blogPost.setIsEdited(false);
        
        // Create a Page containing a single BlogPost
        Page<BlogPost> blogPostPage = new PageImpl<>(Collections.singletonList(blogPost));
        
        // Mock behavior of BlogPostRepository
        when(blogPostRepository.findAll(PageRequest.of(0, 10))).thenReturn(blogPostPage);
        
        // Call the service method
        Page<BlogPostDto> resultPage = blogPostService.getAllBlogPosts(0, 10);
        
        // Extract the first element from the Page
        BlogPostDto blogPostResult = resultPage.getContent().get(0);
        
        // Verify that the repository method was called
        verify(blogPostRepository, times(1)).findAll(PageRequest.of(0, 10));
        
        // Assert the result
        assertEquals(expectedBlogPostDto, blogPostResult);
    }

    @Test
    public void when_getBlogPostByInvalidId_then_throwBlogPostNotFoundException() {
        assertThrows(BlogPostNotFoundException.class, () -> blogPostService.getBlogPostById(999L));
    }

}