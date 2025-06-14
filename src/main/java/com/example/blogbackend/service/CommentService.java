package com.example.blogbackend.service;


import com.example.blogbackend.domain.BlogPost;
import com.example.blogbackend.domain.Comment;
import com.example.blogbackend.dto.CommentDto;
import com.example.blogbackend.dto.CreateCommentDto;
import com.example.blogbackend.exception.BlogPostNotFoundException;
import com.example.blogbackend.exception.CommentNotFoundException;
import com.example.blogbackend.provider.TimeProvider;
import com.example.blogbackend.repository.BlogPostRepository;
import com.example.blogbackend.repository.CommentRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final BlogPostRepository blogPostRepository;
    private final TimeProvider timeProvider;

    public CommentService(CommentRepository commentRepository, BlogPostRepository blogPostRepository, TimeProvider timeProvider) {
        this.commentRepository = commentRepository;
        this.blogPostRepository = blogPostRepository;
	    this.timeProvider = timeProvider;
    }

    @Transactional
    public CommentDto createComment(CreateCommentDto createCommentDto) {
        Long blogPostId = createCommentDto.blogPostId();
        BlogPost blogPost = blogPostRepository.findById(blogPostId).orElseThrow(
                () -> new BlogPostNotFoundException("Blog post with id: " + blogPostId + " not found.")
        );
        CommentDto commentDto = createCommentDto.toDomain();
        Comment comment = Comment.from(commentDto);

        comment.setBlogPost(blogPost);
        comment.setCreatedAt(timeProvider.getNow());

        Comment newComment = commentRepository.save(comment);
        
        blogPost.getComments().add(newComment);
        blogPostRepository.save(blogPost);

        return CommentDto.from(newComment);
    }

    public CommentDto getCommentById(Long id) {
        Comment comment = commentRepository.findById(id).orElseThrow(() -> new CommentNotFoundException(
                "Comment with id: " + id + " not found."
        ));
        return comment.toDTO();
    }
    
    public List<CommentDto> getCommentsByBlogPostId(Long blogPostId) {
        // Check if the blog post exists
        blogPostRepository.findById(blogPostId).orElseThrow(
                () -> new BlogPostNotFoundException("Blog post with id: " + blogPostId + " not found."));
        
        List<Comment> commentList = commentRepository.findByBlogPostId(blogPostId);
        return commentList.stream().map(Comment::toDTO).toList();
    }
}
