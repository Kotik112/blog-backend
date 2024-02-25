package com.example.blogbackend.service;

import com.example.blogbackend.domain.BlogPost;
import com.example.blogbackend.domain.Image;
import com.example.blogbackend.dto.ImageDto;
import com.example.blogbackend.exception.BlogPostNotFoundException;
import com.example.blogbackend.exception.EmptyFileException;
import com.example.blogbackend.exception.ImageUploadException;
import com.example.blogbackend.provider.TimeProvider;
import com.example.blogbackend.repository.BlogPostRepository;
import com.example.blogbackend.repository.ImageRepository;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import org.springframework.http.HttpHeaders;

import java.util.logging.Logger;

@Service
public class ImageService {
	
	public final ImageRepository imageRepository;
	private final BlogPostRepository blogPostRepository;
	private final TimeProvider timeProvider;
	private static final Logger LOGGER = Logger.getLogger(ImageService.class.getName());
	
	
	public ImageService(ImageRepository imageRepository, BlogPostRepository blogPostRepository, TimeProvider timeProvider) {
		this.imageRepository = imageRepository;
		this.blogPostRepository = blogPostRepository;
		this.timeProvider = timeProvider;
	}
	
	public ImageDto uploadImage(MultipartFile file, Long blogPostId) {
		if (file.isEmpty()) {
			throw new EmptyFileException(
					String.format(
							"File %s is empty, cannot upload the image", file.getOriginalFilename()));
		}
		BlogPost blogPost = blogPostRepository.findById(blogPostId).orElseThrow(
				() -> new BlogPostNotFoundException("Blog post with id: " + blogPostId + " not found")
		);
		try {
			Image image = new Image();
			image.setName(file.getOriginalFilename());
			image.setType(file.getContentType());
			image.setCreatedAt(timeProvider.getNow());
			byte[] imageData = file.getBytes();
			image.setImageData(imageData);
			
			blogPost.setImage(image);
			imageRepository.save(image);
			BlogPost updatedBlogPost = blogPostRepository.save(blogPost);
			
			Image savedImage = updatedBlogPost.getImage();
			return ImageDto.toDto(savedImage);
			
		}
		catch (IOException e) {
			throw new ImageUploadException("Error occurred while uploading the image");
		}
	}
	
	public ResponseEntity<ByteArrayResource> getImageById(Long id) {
		Image image = imageRepository.findById(id).orElseThrow(
				() -> new BlogPostNotFoundException("Image with id: " + id + " not found"));
		
		ByteArrayResource resource = new ByteArrayResource(image.getImageData());
		
		// Set the header for the response
		HttpHeaders headers = new HttpHeaders();
		headers.setContentLength(resource.contentLength());
		headers.setContentType(MediaType.parseMediaType(image.getType()));
		return ResponseEntity.ok()
				       .headers(headers)
				       .contentLength(image.getImageData().length)
				       .body(resource);
		
	}
	
	public ResponseEntity<ByteArrayResource> getImageByFilename(String filename) {
		Image image = imageRepository.findByName(filename).orElseThrow(
				() -> new BlogPostNotFoundException("Image with name: " + filename + " not found"));
		
		ByteArrayResource resource = new ByteArrayResource(image.getImageData());
		
		// Set the header for the response
		HttpHeaders headers = new HttpHeaders();
		headers.setContentLength(resource.contentLength());
		headers.setContentType(MediaType.parseMediaType(image.getType()));
		return ResponseEntity.ok()
				       .headers(headers)
				       .contentLength(image.getImageData().length)
				       .body(resource);
	}
}
