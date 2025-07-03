package com.example.blogbackend.service;

import com.example.blogbackend.domain.BlogPost;
import com.example.blogbackend.domain.Image;
import com.example.blogbackend.dto.ImageDto;
import com.example.blogbackend.exception.EmptyFileException;
import com.example.blogbackend.exception.ImageUploadException;
import com.example.blogbackend.provider.TimeProvider;
import com.example.blogbackend.repository.BlogPostRepository;
import com.example.blogbackend.repository.ImageRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;

@ExtendWith(MockitoExtension.class)
class ImageServiceTest {
	@InjectMocks
	ImageService imageService;
	
	@Mock
	BlogPostRepository blogPostRepository;
	
	@Mock
	ImageRepository imageRepository;
	
	@Mock
	TimeProvider timeProvider;
	
	@Test
	void test_uploadImage() {
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
		when(imageRepository.save(any())).thenReturn(image);
		
		MultipartFile file = new MockMultipartFile("file",
		                                           "test.jpg",
		                                           IMAGE_JPEG_VALUE,
		                                           "some data".getBytes()
		);
		ImageDto imageDto = imageService.uploadImage(file, 1L);
		
		// Verify the result
		verify(blogPostRepository).findById(1L);
		verify(blogPostRepository).save(any());
		verify(timeProvider).getNow();
		verify(imageRepository).save(any());
		
		Assertions.assertNotNull(imageDto);
		Assertions.assertEquals("test.jpg", imageDto.name());
		Assertions.assertEquals("image/jpeg", imageDto.type());
		Assertions.assertEquals(createdAt, imageDto.createdAt());
	}

	@Test
	void test_uploadImage_emptyFile() {
		MultipartFile file = new MockMultipartFile("file", "test.jpg", IMAGE_JPEG_VALUE, new byte[0]);

		assertThrows(EmptyFileException.class, () -> {
			imageService.uploadImage(file, 1L);
		});
	}

	@Test
	void test_uploadImage_fileUploadException() {
		BlogPost blogPost = new BlogPost();
		blogPost.setId(1L);

		MultipartFile file = new MockMultipartFile("file", "test.jpg", IMAGE_JPEG_VALUE, new byte[0]);

		assertThrows(EmptyFileException.class, () -> {
			imageService.uploadImage(file, 1L);
		});
	}

	@Test
	void test_uploadImage_ImageUploadException() throws IOException {
		BlogPost blogPost = new BlogPost();
		blogPost.setId(1L);
		when(blogPostRepository.findById(1L)).thenReturn(Optional.of(blogPost));
		when(timeProvider.getNow()).thenReturn(Instant.now());

		MultipartFile file = mock(MultipartFile.class);
		when(file.isEmpty()).thenReturn(false);
		when(file.getBytes()).thenThrow(IOException.class);

		assertThrows(ImageUploadException.class, () -> {
			imageService.uploadImage(file, 1L);
		});
	}
	
	@Test
	void test_getImageById() {
		Image image = new Image();
		image.setId(1L);
		image.setName("test.jpg");
		image.setType(IMAGE_JPEG_VALUE);
		image.setImageData("some data".getBytes());
		Instant createdAt = Instant.now();
		image.setCreatedAt(createdAt);
		
		// Mocks
		when(imageRepository.findById(1L)).thenReturn(Optional.of(image));
		
		ResponseEntity<ByteArrayResource> imageDto = imageService.getImageById(1L);
		
		// Verify the result
		verify(imageRepository).findById(1L);
		
		Assertions.assertNotNull(imageDto);
		Assertions.assertEquals("some data".length(), Objects.requireNonNull(imageDto.getBody()).contentLength());
		Assertions.assertEquals("image/jpeg", Objects.requireNonNull(imageDto.getHeaders().getContentType()).toString());
	}
}
