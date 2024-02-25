package com.example.blogbackend.service;

import com.example.blogbackend.domain.BlogPost;
import com.example.blogbackend.domain.Image;
import com.example.blogbackend.dto.ImageDto;
import com.example.blogbackend.provider.TimeProvider;
import com.example.blogbackend.repository.BlogPostRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;

@RunWith(MockitoJUnitRunner.class)
public class ImageServiceTest {
	@InjectMocks
	ImageService imageService;
	
	@Mock
	BlogPostRepository blogPostRepository;
	
	@Mock
	TimeProvider timeProvider;
	
	@Test
	public void test_uploadImage() {
		BlogPost blogPost = new BlogPost();
		blogPost.setId(1L);
		
		Image image = new Image();
		image.setId(1L);
		image.setName("test.jpg");
		image.setType(IMAGE_JPEG_VALUE);
		image.setImageData("some data".getBytes());
		Instant createdAt = Instant.now();
		image.setCreatedAt(createdAt);
		
		// Mocks
		when(blogPostRepository.findById(1L)).thenReturn(Optional.of(blogPost));
		when(blogPostRepository.save(any())).thenReturn(blogPost);
		when(timeProvider.getNow()).thenReturn(createdAt);
		
		MultipartFile file = new MockMultipartFile("file",
		                                           "test.jpg",
		                                           IMAGE_JPEG_VALUE,
		                                           "some data".getBytes()
		);
		ImageDto imageDto = imageService.uploadImage(file, 1L);
		
		// Verify the result
		verify(blogPostRepository).findById(1L);
		verify(blogPostRepository).save(any());
		
		assertNotNull(imageDto);
		assertEquals("test.jpg", imageDto.name());
		assertEquals("image/jpeg", imageDto.type());
		assertEquals(createdAt, imageDto.createdAt());
	}
	
}
