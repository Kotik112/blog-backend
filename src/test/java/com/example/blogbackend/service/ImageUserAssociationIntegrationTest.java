package com.example.blogbackend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.example.blogbackend.domain.BlogPost;
import com.example.blogbackend.domain.Image;
import com.example.blogbackend.domain.User;
import com.example.blogbackend.dto.ImageDto;
import com.example.blogbackend.enums.Role;
import com.example.blogbackend.repository.BlogPostRepository;
import com.example.blogbackend.repository.ImageRepository;
import com.example.blogbackend.repository.UserRepository;
import com.example.blogbackend.utils.SpringBootComponentTest;
import java.security.Principal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

class ImageUserAssociationIntegrationTest extends SpringBootComponentTest {

  @Autowired private ImageService imageService;
  @Autowired private BlogPostRepository blogPostRepository;
  @Autowired private UserRepository userRepository;
  @Autowired private ImageRepository imageRepository;

  @Test
  @WithMockUser(username = "integrationtestuser", roles = "USER")
  @Transactional
  void test_uploadImage_associatesWithCorrectUser() throws Exception {
    // Create a test user
    User testUser = new User();
    testUser.setUsername("integrationtestuser");
    testUser.setEmail("integration@test.com");
    testUser.setPassword("password");
    testUser.setRole(Role.USER);
    testUser.setCreatedAt(java.time.Instant.now());
    testUser = userRepository.save(testUser);

    // Create a test blog post
    BlogPost blogPost = new BlogPost();
    blogPost.setTitle("Test Post");
    blogPost.setContent("Test Content");
    blogPost.setCreatedBy(testUser);
    blogPost.setCreatedAt(java.time.Instant.now());
    blogPost = blogPostRepository.save(blogPost);

    // Create a mock Principal for the user
    Principal principal = () -> "integrationtestuser";

    // Upload an image
    MockMultipartFile file =
        new MockMultipartFile(
            "image", "test-integration.jpg", "image/jpeg", "test image data".getBytes());

    ImageDto result = imageService.uploadImage(file, blogPost.getId(), principal);

    // Verify the result
    assertNotNull(result);
    assertNotNull(result.createdBy());
    assertEquals("integrationtestuser", result.createdBy().username());
    assertEquals("integration@test.com", result.createdBy().email());

    // Verify in database
    Image savedImage = imageRepository.findById(result.id()).orElseThrow();
    assertNotNull(savedImage.getCreatedBy());
    assertEquals("integrationtestuser", savedImage.getCreatedBy().getUsername());
    assertEquals(testUser.getId(), savedImage.getCreatedBy().getId());
  }
}
