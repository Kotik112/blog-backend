package com.example.blogbackend.controller;

import com.example.blogbackend.dto.BlogPostDto;
import com.example.blogbackend.dto.CreateBlogPostDto;
import com.example.blogbackend.dto.ImageDto;
import com.example.blogbackend.utils.SpringBootComponentTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.is;

@RunWith(SpringRunner.class)
public class ImageControllerIntegrationTest extends SpringBootComponentTest {
	
	@Autowired
	MockMvc mvc;
	
	@Autowired
	ObjectMapper mapper;
	
	@Test
	public void when_addImageToBlogPost_then_imageIsUploaded() throws Exception {
		// Create a Blog Post
		CreateBlogPostDto createBlogPostDto = new CreateBlogPostDto("Test Blog Post", "This is a test blog post.");
		MvcResult createPostResult = mvc.perform(post(BASE_BLOG_POST_URL)
				                                         .contentType(APPLICATION_JSON)
				                                         .content(objectMapper.writeValueAsString(createBlogPostDto)))
				                             .andExpect(status().isCreated())
				                             .andReturn();
		
		BlogPostDto blogPostDto = objectMapper.readValue(createPostResult.getResponse().getContentAsString(), BlogPostDto.class);
		assertNotNull(blogPostDto.id());
		
		// Create an Image DTO
		MockMultipartFile file = new MockMultipartFile(
				"image", "test.jpg", IMAGE_JPEG_VALUE, new byte[] {0x01, 0x02, 0x03});
		
		MvcResult uploadImageResult = mvc.perform(multipart(BASE_IMAGE_URL + "/blog-post/" + blogPostDto.id())
				                                          .file(file)
				                                          .contentType(MULTIPART_FORM_DATA))
				                              .andExpect(status().isOk())
				                              .andExpect(jsonPath("$.type", is(IMAGE_JPEG_VALUE)))
				                              .andExpect(jsonPath("$.name", is("test.jpg")))
				                              .andReturn();
		
		ImageDto uploadedImage = objectMapper.readValue(uploadImageResult.getResponse().getContentAsString(), ImageDto.class);
		
		// Assert the uploaded image details
		assertNotNull(uploadedImage);
		assertEquals(IMAGE_JPEG_VALUE, uploadedImage.type());
		assertEquals("test.jpg", uploadedImage.name());
		assertNotNull(uploadedImage.createdAt());
	}
	
	@Test
	public void when_addImageToNonExistingBlogPost_then_imageUploadFails() throws Exception {
		MockMultipartFile file = new MockMultipartFile(
				"image", "test.jpg", IMAGE_JPEG_VALUE, new byte[] {0x01, 0x02, 0x03});
		
		mvc.perform(multipart(BASE_IMAGE_URL + "/blog-post/100")
				            .file(file)
				            .contentType(MULTIPART_FORM_DATA))
		   .andExpect(status().isNotFound());
	}
	
	@Test
	public void when_addEmptyImageToBlogPost_then_imageUploadFails() throws Exception {
		MockMultipartFile file = new MockMultipartFile(
				"image", "test.jpg", IMAGE_JPEG_VALUE, new byte[0]);
		
		mvc.perform(multipart(BASE_IMAGE_URL + "/blog-post/1")
				            .file(file)
				            .contentType(MULTIPART_FORM_DATA))
		   .andExpect(status().isBadRequest());
	}
	
	@Test
	public void when_getImageById_then_imageIsReturned() throws Exception {
		// Create a Blog Post
		CreateBlogPostDto createBlogPostDto = new CreateBlogPostDto("Test Blog Post", "This is a test blog post.");
		MvcResult createPostResult = mvc.perform(post(BASE_BLOG_POST_URL)
				                                         .contentType(APPLICATION_JSON)
				                                         .content(objectMapper.writeValueAsString(createBlogPostDto)))
				                             .andExpect(status().isCreated())
				                             .andReturn();
		
		BlogPostDto blogPostDto = objectMapper.readValue(createPostResult.getResponse().getContentAsString(), BlogPostDto.class);
		assertNotNull(blogPostDto.id());
		
		// Upload an Image to the Blog Post
		MockMultipartFile file = new MockMultipartFile(
				"image", "test.jpg", IMAGE_JPEG_VALUE, new byte[]{0x01, 0x02, 0x03});
		
		MvcResult uploadImageResult = mvc.perform(multipart(BASE_IMAGE_URL + "/blog-post/" + blogPostDto.id())
				                                          .file(file)
				                                          .contentType(MULTIPART_FORM_DATA))
				                              .andExpect(status().isOk())
				                              .andReturn();
		
		ImageDto uploadedImage = objectMapper.readValue(uploadImageResult.getResponse().getContentAsString(), ImageDto.class);
		assertNotNull(uploadedImage);
		
		// Get the Image by its ID
		MvcResult getImageResult = mvc.perform(get(BASE_IMAGE_URL + "/" + uploadedImage.id()))
				                           .andExpect(status().isOk())
				                           .andReturn();
		byte[] imageData = getImageResult.getResponse().getContentAsByteArray();
		
		// Assert the image details
		assertNotNull(imageData);
		assertEquals(3, imageData.length);
	}
}
