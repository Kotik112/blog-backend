package com.example.blogbackend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.blogbackend.domain.BlogPost;
import com.example.blogbackend.domain.User;
import com.example.blogbackend.dto.BlogPostDto;
import com.example.blogbackend.dto.CreateBlogPostDto;
import com.example.blogbackend.enums.Role;
import com.example.blogbackend.exception.BlogPostNotFoundException;
import com.example.blogbackend.exception.ImageUploadException;
import com.example.blogbackend.provider.TimeProvider;
import com.example.blogbackend.repository.BlogPostRepository;
import com.example.blogbackend.repository.UserRepository;
import java.io.IOException;
import java.security.Principal;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class BlogPostServiceTest {

  @InjectMocks BlogPostService blogPostService;
  @Mock BlogPostRepository blogPostRepository;
  @Mock UserRepository userRepository;
  @Mock TimeProvider timeProvider;
  @Mock ImageService imageService;

  //    @Mock
  //    TimeProvider timeProvider;

  @Test
  void test_createBlogPost() {
    CreateBlogPostDto createBlogPostDTO = new CreateBlogPostDto("Test title", "Test content");

    // Expected output data
    BlogPostDto expectedBlogPostDto =
        new BlogPostDto(
            1L,
            "Test title",
            "Test content",
            Instant.parse("2023-04-04T12:42:00Z"),
            "testuser",
            null,
            false,
            Set.of(),
            Set.of(),
            null);

    User mockUser = spy(User.class);
    mockUser.setId(1L);
    mockUser.setUsername("testuser");
    mockUser.setRole(Role.USER);
    mockUser.setPassword("testPassword");

    BlogPost blogPost = new BlogPost();
    blogPost.setId(1L);
    blogPost.setTitle("Test title");
    blogPost.setContent("Test content");
    blogPost.setCreatedBy(mockUser);
    blogPost.setCreatedAt(Instant.parse("2023-04-04T12:42:00Z"));
    blogPost.setIsEdited(false);

    Principal mockPrincipal = mock(Principal.class);

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
            "testuser",
            null,
            false,
            Collections.emptySet(),
            Collections.emptySet(),
            null);

    User mockUser = spy(User.class);
    mockUser.setId(1L);
    mockUser.setUsername("testuser");
    mockUser.setRole(Role.USER);
    mockUser.setPassword("testPassword");

    BlogPost blogPost = new BlogPost();
    blogPost.setId(1L);
    blogPost.setTitle("Test title");
    blogPost.setContent("Test content");
    blogPost.setCreatedBy(mockUser);
    blogPost.setCreatedAt(Instant.parse("2023-04-04T12:42:00Z"));
    blogPost.setIsEdited(false);

    // Create a Page containing a single BlogPost
    Page<BlogPost> blogPostPage = new PageImpl<>(Collections.singletonList(blogPost));
    when(blogPostRepository.findAll(any(Pageable.class))).thenReturn(blogPostPage);

    Page<BlogPostDto> resultPage = blogPostService.getAllBlogPosts(0, 10);

    BlogPostDto blogPostResult = resultPage.getContent().get(0);

    verify(blogPostRepository, times(1)).findAll(any(Pageable.class));
    verify(mockUser, times(1)).getUsername();

    // Assert the result
    assertEquals(expectedBlogPostDto, blogPostResult);
  }

  @Test
  void when_getBlogPostByInvalidId_then_throwBlogPostNotFoundException() {
    assertThrows(BlogPostNotFoundException.class, () -> blogPostService.getBlogPostById(999L));
  }

  @Test
  void getBlogPostsByUser_shouldReturnPagedDtos() {
    // Arrange
    String username = "john";
    int page = 0;
    int size = 2;

    User mockUser = new User();
    mockUser.setId(1L);
    mockUser.setUsername(username);

    BlogPost blogPost1 = new BlogPost();
    blogPost1.setId(101L);
    blogPost1.setTitle("Post 1");

    BlogPost blogPost2 = new BlogPost();
    blogPost2.setId(102L);
    blogPost2.setTitle("Post 2");

    Page<BlogPost> blogPostPage = new PageImpl<>(List.of(blogPost1, blogPost2));

    when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));
    when(blogPostRepository.findAllByCreatedBy_Username(eq(username), any(PageRequest.class)))
        .thenReturn(blogPostPage);

    // Act
    Page<BlogPostDto> result = blogPostService.getBlogPostsByUser(username, page, size);

    // Assert
    assertEquals(2, result.getContent().size());
    assertEquals("Post 1", result.getContent().get(0).title());
    assertEquals("Post 2", result.getContent().get(1).title());

    verify(userRepository).findByUsername(username);
    verify(blogPostRepository).findAllByCreatedBy_Username(eq(username), any(PageRequest.class));
  }

  @Test
  void test_createBlogPost_withInvalidImage_throwsImageUploadException() throws IOException {
    // Arrange
    CreateBlogPostDto createBlogPostDTO = new CreateBlogPostDto("Test title", "Test content");
    Principal mockPrincipal = mock(Principal.class);
    MultipartFile mockFile = mock(MultipartFile.class);
    User mockUser = mock(User.class);

    when(mockPrincipal.getName()).thenReturn("testuser");
    when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(mockUser));
    when(mockFile.isEmpty()).thenReturn(false);
    when(imageService.prepareImageForUpload(mockFile, mockUser))
        .thenThrow(new IOException("Simulated failure"));

    // Act & Assert
    ImageUploadException exception =
        assertThrows(
            ImageUploadException.class,
            () -> blogPostService.createBlogPost(createBlogPostDTO, mockFile, mockPrincipal));

    assertEquals("Error occurred while preparing the image for upload", exception.getMessage());

    verify(mockPrincipal, times(1)).getName();
    verify(userRepository, times(1)).findByUsername("testuser");
    verify(mockFile, times(1)).isEmpty();
    verify(imageService, times(1)).prepareImageForUpload(mockFile, mockUser);

    // Ensure time and blog post repository are not hit due to early failure
    verifyNoInteractions(timeProvider, blogPostRepository);
  }
}
