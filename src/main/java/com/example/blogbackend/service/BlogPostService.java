package com.example.blogbackend.service;


import com.example.blogbackend.domain.BlogPost;
import com.example.blogbackend.domain.Image;
import com.example.blogbackend.dto.BlogPostDto;
import com.example.blogbackend.dto.CreateBlogPostDto;
import com.example.blogbackend.exception.BlogPostNotFoundException;
import com.example.blogbackend.exception.ImageUploadException;
import com.example.blogbackend.repository.BlogPostRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BlogPostService {

    private final BlogPostRepository blogPostRepository;
    private final ImageService imageService;

    public BlogPostService(BlogPostRepository blogPostRepository, ImageService imageService) {
        this.blogPostRepository = blogPostRepository;
	    this.imageService = imageService;
    }

    public BlogPostDto createBlogPost(CreateBlogPostDto blogPostDTO, MultipartFile image) {
        BlogPost blogPost = blogPostDTO.toDomain();
        
        if (image != null && !image.isEmpty()) {
            try {
                Image preparedImage = imageService.prepareImageForUpload(image);
                blogPost.setImage(preparedImage);
            } catch (IOException e) {
                throw new ImageUploadException("Error occurred while preparing the image for upload");
            }
        }
        
        BlogPost savedBlogPost = blogPostRepository.save(blogPost);
        return BlogPostDto.toDto(savedBlogPost);
    }

    public Page<BlogPostDto> getAllBlogPosts(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<BlogPost> blogPostPage = blogPostRepository.findAll(pageRequest);
        
        List<BlogPostDto> blogPostDtoList = blogPostPage.getContent().stream()
                .map(BlogPostDto::toDto)
                .collect(Collectors.toList());
        
        return new PageImpl<>(blogPostDtoList, pageRequest, blogPostPage.getTotalElements());
    }

    public BlogPostDto getBlogPostById(Long id) {
        BlogPost blogPost = blogPostRepository.findById(id).orElseThrow(
                () -> new BlogPostNotFoundException("Blog post with id: " + id + " not found.")
        );
        return BlogPostDto.toDto(blogPost);
    }
}
