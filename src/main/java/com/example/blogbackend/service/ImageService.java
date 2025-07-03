package com.example.blogbackend.service;

import com.example.blogbackend.domain.BlogPost;
import com.example.blogbackend.domain.Image;
import com.example.blogbackend.dto.ImageDto;
import com.example.blogbackend.exception.BlogPostNotFoundException;
import com.example.blogbackend.exception.EmptyFileException;
import com.example.blogbackend.exception.ImageNotFoundException;
import com.example.blogbackend.exception.ImageUploadException;
import com.example.blogbackend.provider.TimeProvider;
import com.example.blogbackend.repository.BlogPostRepository;
import com.example.blogbackend.repository.ImageRepository;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageService {
  private final Logger logger = LoggerFactory.getLogger(ImageService.class);

  private final ImageRepository imageRepository;
  private final BlogPostRepository blogPostRepository;
  private final TimeProvider timeProvider;

  public ImageService(
      ImageRepository imageRepository,
      BlogPostRepository blogPostRepository,
      TimeProvider timeProvider) {
    this.imageRepository = imageRepository;
    this.blogPostRepository = blogPostRepository;
    this.timeProvider = timeProvider;
  }

  public ImageDto uploadImage(MultipartFile file, Long blogPostId) {
    if (file != null && file.isEmpty()) {
      logger.warn("File {} is empty, cannot upload the image", file.getOriginalFilename());
      throw new EmptyFileException(
          String.format("File %s is empty, cannot upload the image", file.getOriginalFilename()));
    }
    BlogPost blogPost =
        blogPostRepository
            .findById(blogPostId)
            .orElseThrow(
                () ->
                    new BlogPostNotFoundException(
                        "Blog post with id: " + blogPostId + " not found"));
    logger.info("Uploading image for blog post with ID: {}", blogPostId);
    try {
      Image image = new Image();
      if (file == null || file.isEmpty()) {
        logger.info("File is empty, cannot upload the image");
        throw new EmptyFileException("File is empty, cannot upload the image");
      }
      image.setName(file.getOriginalFilename());
      image.setType(file.getContentType());
      image.setCreatedAt(timeProvider.getNow());
      byte[] imageData = file.getBytes();
      image.setImageData(imageData);

      blogPost.setImage(image);
      imageRepository.save(image);
      BlogPost updatedBlogPost = blogPostRepository.save(blogPost);
      logger.info("Image uploaded successfully for blog post with ID: {}", updatedBlogPost.getId());
      Image savedImage = updatedBlogPost.getImage();
      return ImageDto.toDto(savedImage);

    } catch (IOException e) {
      logger.info("Error occurred while uploading the image: {}", e.getMessage());
      throw new ImageUploadException("Error occurred while uploading the image");
    }
  }

  public ResponseEntity<ByteArrayResource> getImageById(Long id) {
    Image image =
        imageRepository
            .findById(id)
            .orElseThrow(() -> new ImageNotFoundException("Image with id: " + id + " not found"));
    logger.debug("Image type: {}", image.getType());

    return getResponseEntity(image);
  }

  public ResponseEntity<ByteArrayResource> getImageByFilename(String filename) {
    Image image =
        imageRepository
            .findByName(filename)
            .orElseThrow(
                () -> new ImageNotFoundException("Image with name: " + filename + " not found"));

    return getResponseEntity(image);
  }

  private ResponseEntity<ByteArrayResource> getResponseEntity(Image image) {
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

  protected Image prepareImageForUpload(MultipartFile file) throws IOException {
    if (file.isEmpty()) {
      throw new EmptyFileException("File is empty, cannot upload the image");
    }

    Image image = new Image();
    image.setName(file.getOriginalFilename());
    image.setType(file.getContentType());
    image.setCreatedAt(timeProvider.getNow());
    image.setImageData(file.getBytes());

    return image;
  }
}
