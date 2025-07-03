package com.example.blogbackend.service;

import com.example.blogbackend.domain.BlogPost;
import com.example.blogbackend.domain.Image;
import com.example.blogbackend.domain.User;
import com.example.blogbackend.dto.BlogPostDto;
import com.example.blogbackend.dto.CreateBlogPostDto;
import com.example.blogbackend.exception.BlogPostNotFoundException;
import com.example.blogbackend.exception.ImageUploadException;
import com.example.blogbackend.provider.TimeProvider;
import com.example.blogbackend.repository.BlogPostRepository;
import com.example.blogbackend.repository.UserRepository;
import java.io.IOException;
import java.security.Principal;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class BlogPostService {
  private final Logger logger = LoggerFactory.getLogger(BlogPostService.class);
  private final BlogPostRepository blogPostRepository;
  private final ImageService imageService;
  private final TimeProvider timeProvider;
  private final UserRepository userRepository;

  public BlogPostService(
      BlogPostRepository blogPostRepository,
      ImageService imageService,
      TimeProvider timeProvider,
      UserRepository userRepository) {
    this.blogPostRepository = blogPostRepository;
    this.imageService = imageService;
    this.timeProvider = timeProvider;
    this.userRepository = userRepository;
  }

  /**
   * Creates a new blog post with an optional image.
   *
   * @param blogPostDTO the DTO containing the details of the blog post to be created
   * @param image the optional image file to be associated with the blog post
   * @return the created BlogPostDto
   */
  public BlogPostDto createBlogPost(
      CreateBlogPostDto blogPostDTO, MultipartFile image, Principal principal) {
    BlogPost blogPost = blogPostDTO.toDomain();
    logger.info("Creating blog post with title: {}", blogPost.getTitle());
    if (image != null && !image.isEmpty()) {
      try {
        Image preparedImage = imageService.prepareImageForUpload(image);
        blogPost.setImage(preparedImage);
      } catch (IOException e) {
        logger.debug("Error occurred while preparing the image for upload: {}", e.getMessage());
        throw new ImageUploadException("Error occurred while preparing the image for upload");
      }
    } else {
      logger.debug("No image provided for blog post with title: {}", blogPost.getTitle());
    }
    blogPost.setCreatedAt(timeProvider.getNow());
    User user =
        userRepository
            .findByUsername(principal.getName())
            .orElseThrow(() -> new RuntimeException("User not found: " + principal.getName()));
    blogPost.setCreatedBy(user);

    BlogPost savedBlogPost = blogPostRepository.save(blogPost);
    logger.debug("Blog post with ID: {} created successfully", savedBlogPost.getId());
    return BlogPostDto.toDto(savedBlogPost);
  }

  /**
   * Retrieves all blog posts with pagination.
   *
   * @param page the page number to retrieve
   * @param size the number of blog posts per page
   * @return a Page containing BlogPostDto objects
   */
  public Page<BlogPostDto> getAllBlogPosts(int page, int size) {
    PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
    Page<BlogPost> blogPostPage = blogPostRepository.findAll(pageRequest);
    logger.info(
        "Retrieved {} blog posts for page {} with size {}",
        blogPostPage.getTotalElements(),
        page,
        size);

    List<BlogPostDto> blogPostDtoList =
        blogPostPage.getContent().stream().map(BlogPostDto::toDto).toList();

    return new PageImpl<>(blogPostDtoList, pageRequest, blogPostPage.getTotalElements());
  }

  /**
   * Retrieves a blog post by its ID.
   *
   * @param id the ID of the blog post to retrieve
   * @return the BlogPostDto corresponding to the given ID
   */
  public BlogPostDto getBlogPostById(Long id) {
    BlogPost blogPost =
        blogPostRepository
            .findById(id)
            .orElseThrow(
                () -> new BlogPostNotFoundException("Blog post with id: " + id + " not found."));
    logger.info("Retrieved blog post with ID: {}", id);
    return BlogPostDto.toDto(blogPost);
  }
}
