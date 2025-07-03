package com.example.blogbackend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.blogbackend.domain.BlogPost;
import com.example.blogbackend.domain.User;
import com.example.blogbackend.dto.BlogPostDto;
import com.example.blogbackend.dto.CreateBlogPostDto;
import com.example.blogbackend.exception.BlogPostNotFoundException;
import com.example.blogbackend.provider.TimeProvider;
import com.example.blogbackend.repository.BlogPostRepository;
import com.example.blogbackend.repository.UserRepository;
import java.security.Principal;
import java.time.Instant;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class BlogPostServiceTest {

  @InjectMocks BlogPostService blogPostService;
  @Mock BlogPostRepository blogPostRepository;
  @Mock UserRepository userRepository;
  @Mock TimeProvider timeProvider;

  //    @Mock
  //    TimeProvider timeProvider;

  @Test
  void test_createBlogPost() {
    // Create the input data
    CreateBlogPostDto createBlogPostDTO = new CreateBlogPostDto("Test title", "Test content");

    // Expected output data
    BlogPostDto expectedBlogPostDto =
        new BlogPostDto(
            1L,
            "Test title",
            "Test content",
            Instant.parse("2023-04-04T12:42:00Z"),
            null,
            false,
            Set.of(),
            Set.of(),
            null);

    BlogPost blogPost = new BlogPost();
    blogPost.setId(1L);
    blogPost.setTitle("Test title");
    blogPost.setContent("Test content");
    blogPost.setCreatedAt(Instant.parse("2023-04-04T12:42:00Z"));
    blogPost.setIsEdited(false);

    Principal mockPrincipal = mock(Principal.class);
    User mockUser = new User();
    mockUser.setId(1L);
    mockUser.setUsername("testuser");

    when(blogPostRepository.save(any())).thenReturn(blogPost);
    when(mockPrincipal.getName()).thenReturn("testuser");
    when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(mockUser));
    when(timeProvider.getNow()).thenReturn(Instant.parse("2023-04-04T12:42:00Z"));

    BlogPostDto blogPostResult =
        blogPostService.createBlogPost(createBlogPostDTO, null, mockPrincipal);

    verify(blogPostRepository, times(1)).save(any());
    verify(mockPrincipal, times(1)).getName();
    verify(userRepository, times(1)).findByUsername("testuser");
    verify(timeProvider, times(1)).getNow();

    assertEquals(expectedBlogPostDto, blogPostResult);
  }

  @Test
  void when_getAllBlogPosts_then_returnBlogPosts() {
    // Expected output data
    BlogPostDto expectedBlogPostDto =
        new BlogPostDto(
            1L,
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
    when(blogPostRepository.findAll(any(Pageable.class))).thenReturn(blogPostPage);

    // Call the service method
    Page<BlogPostDto> resultPage = blogPostService.getAllBlogPosts(0, 10);

    // Extract the first element from the Page
    BlogPostDto blogPostResult = resultPage.getContent().get(0);

    // Verify that the repository method was called
    verify(blogPostRepository, times(1)).findAll(any(Pageable.class));

    // Assert the result
    assertEquals(expectedBlogPostDto, blogPostResult);
  }

  @Test
  void when_getBlogPostByInvalidId_then_throwBlogPostNotFoundException() {
    assertThrows(BlogPostNotFoundException.class, () -> blogPostService.getBlogPostById(999L));
  }
}
