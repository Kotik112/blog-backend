package com.example.blogbackend.controller;

import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.blogbackend.dto.BlogPostDto;
import com.example.blogbackend.dto.ImageDto;
import com.example.blogbackend.utils.SpringBootComponentTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

class ImageControllerIntegrationTest extends SpringBootComponentTest {

  @Autowired MockMvc mvc;

  @WithMockUser(username = "testUser", roles = "USER")
  @Test
  @Transactional
  void when_addImageToBlogPost_then_imageIsUploaded() throws Exception {
    // Step 1: Create blog post
    MockMultipartFile title =
        new MockMultipartFile("title", "", "text/plain", "Test Blog Post".getBytes());
    MockMultipartFile content =
        new MockMultipartFile("content", "", "text/plain", "This is a test.".getBytes());

    MvcResult createPostResult =
        mvc.perform(
                multipart(BASE_BLOG_POST_URL)
                    .file(title)
                    .file(content)
                    .contentType(MULTIPART_FORM_DATA))
            .andExpect(status().isCreated())
            .andReturn();

    BlogPostDto blogPostDto =
        objectMapper.readValue(
            createPostResult.getResponse().getContentAsString(), BlogPostDto.class);
    Assertions.assertNotNull(blogPostDto.id());
    System.out.println("Created Blog Post ID: " + blogPostDto.id());

    MockMultipartFile file =
        new MockMultipartFile("image", "test.jpg", IMAGE_JPEG_VALUE, "not-empty".getBytes());

    MvcResult uploadImageResult =
        mvc.perform(
                multipart(BASE_IMAGE_URL + "/blog-post/" + blogPostDto.id()) // sends POST
                    .file(file)
                    .contentType(MULTIPART_FORM_DATA))
            .andExpect(status().isCreated())
            .andReturn();

    ImageDto uploadedImage =
        objectMapper.readValue(
            uploadImageResult.getResponse().getContentAsString(), ImageDto.class);
    Assertions.assertNotNull(uploadedImage.createdAt());
  }

  @Test
  void when_addImageToNonExistingBlogPost_then_imageUploadFails() throws Exception {
    MockMultipartFile file =
        new MockMultipartFile("image", "test.jpg", IMAGE_JPEG_VALUE, new byte[] {0x01, 0x02, 0x03});

    mvc.perform(
            multipart(BASE_IMAGE_URL + "/blog-post/100")
                .file(file)
                .contentType(MULTIPART_FORM_DATA))
        .andExpect(status().isNotFound());
  }

  @Test
  void when_addEmptyImageToBlogPost_then_imageUploadFails() throws Exception {
    MockMultipartFile file =
        new MockMultipartFile("image", "test.jpg", IMAGE_JPEG_VALUE, new byte[0]);

    mvc.perform(
            multipart(BASE_IMAGE_URL + "/blog-post/1").file(file).contentType(MULTIPART_FORM_DATA))
        .andExpect(status().isBadRequest());
  }

  @WithMockUser(username = "testUser", roles = "USER")
  @Test
  @Transactional
  void when_getImageById_then_imageIsReturned() throws Exception {
    // Create a Blog Post
    MockMultipartFile titlePart =
        new MockMultipartFile("title", "", "text/plain", "Test Blog Post".getBytes());
    MockMultipartFile contentPart =
        new MockMultipartFile("content", "", "text/plain", "This is a test blog post.".getBytes());

    MvcResult createPostResult =
        mvc.perform(multipart(BASE_BLOG_POST_URL).file(titlePart).file(contentPart))
            .andExpect(status().isCreated())
            .andReturn();

    BlogPostDto blogPostDto =
        objectMapper.readValue(
            createPostResult.getResponse().getContentAsString(), BlogPostDto.class);
    Assertions.assertNotNull(blogPostDto.id());
    System.out.println(
        "Create post response: " + createPostResult.getResponse().getContentAsString());

    // Upload an Image to the Blog Post
    MockMultipartFile file =
        new MockMultipartFile("image", "test.jpg", IMAGE_JPEG_VALUE, new byte[] {0x01, 0x02, 0x03});

    MvcResult uploadImageResult =
        mvc.perform(
                multipart(BASE_IMAGE_URL + "/blog-post/" + blogPostDto.id())
                    .file(file)
                    .with(
                        request -> {
                          request.setMethod("POST");
                          return request;
                        })
                    .contentType(MULTIPART_FORM_DATA))
            .andReturn();
    System.out.println(
        "Upload image response: " + uploadImageResult.getResponse().getContentAsString());
    ImageDto uploadedImage =
        objectMapper.readValue(
            uploadImageResult.getResponse().getContentAsString(), ImageDto.class);
    Assertions.assertNotNull(uploadedImage);

    // Get the Image by its ID
    MvcResult getImageResult =
        mvc.perform(get(BASE_IMAGE_URL + "/" + uploadedImage.id()))
            .andExpect(status().isOk())
            .andReturn();
    byte[] imageData = getImageResult.getResponse().getContentAsByteArray();

    // Assert the image details
    Assertions.assertNotNull(imageData);
    Assertions.assertEquals(3, imageData.length);
  }
}
