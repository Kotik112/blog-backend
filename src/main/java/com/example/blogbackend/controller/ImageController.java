package com.example.blogbackend.controller;

import com.example.blogbackend.dto.ImageDto;
import com.example.blogbackend.service.ImageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/images")
public class ImageController {
	
	private final ImageService imageService;
	
	public ImageController(ImageService imageService) {
		this.imageService = imageService;
	}
	
	@PostMapping("/blog-post/{blogPostId}")
	public ImageDto uploadImage(@RequestParam("image") MultipartFile file, @PathVariable("blogPostId") Long blogPostId) {
		return imageService.uploadImage(file, blogPostId);
	}
	
	@GetMapping("/{filename}")
	public ResponseEntity<?> downloadImage(String filename) {
		// return imageService.getImageById(id);
		return null;
	}
}
