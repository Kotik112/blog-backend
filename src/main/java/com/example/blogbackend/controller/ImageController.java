package com.example.blogbackend.controller;

import com.example.blogbackend.dto.ImageDto;
import com.example.blogbackend.service.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@SuppressWarnings("unused")
@Tag(name = "Images", description = "Operations related to images")
public class ImageController {
	
	private final ImageService imageService;
	
	public ImageController(ImageService imageService) {
		this.imageService = imageService;
	}

	/**
	 * Uploads an image associated with a blog post.
	 *
	 * @param file the image file to be uploaded
	 * @param blogPostId the ID of the blog post to associate the image with
	 * @return the uploaded ImageDto
	 */
	@Operation(summary = "Upload an image for a blog post", description = "Uploads an image associated with a blog post.")
	@PostMapping("/blog-post/{blogPostId}")
	public ImageDto uploadImage(@RequestParam("image") MultipartFile file, @PathVariable("blogPostId") Long blogPostId) {
		return imageService.uploadImage(file, blogPostId);
	}

	/**
	 * Downloads an image by its ID.
	 *
	 * @param id the ID of the image to be downloaded
	 * @return the ResponseEntity containing the image data
	 */
	@Operation(summary = "Download an image by ID", description = "Downloads an image by its ID.")
	@GetMapping("/{id}")
	public ResponseEntity<?> downloadImage(@PathVariable("id") Long id) {
		return imageService.getImageById(id);
	}

	/**
	 * Downloads an image by its filename.
	 *
	 * @param filename the filename of the image to be downloaded
	 * @return the ResponseEntity containing the image data
	 */
	@Operation(summary = "Download an image by filename", description = "Downloads an image by its filename.")
	@GetMapping("filename/{filename}")
	public ResponseEntity<?> downloadImageByFilename(@PathVariable("filename") String filename) {
		return imageService.getImageByFilename(filename);
	}
}
