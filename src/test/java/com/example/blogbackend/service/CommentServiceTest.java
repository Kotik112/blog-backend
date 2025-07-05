package com.example.blogbackend.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.blogbackend.domain.BlogPost;
import com.example.blogbackend.domain.Comment;
import com.example.blogbackend.dto.CommentDto;
import com.example.blogbackend.dto.CreateCommentDto;
import com.example.blogbackend.exception.BlogPostNotFoundException;
import com.example.blogbackend.exception.CommentNotFoundException;
import com.example.blogbackend.provider.TimeProvider;
import com.example.blogbackend.repository.BlogPostRepository;
import com.example.blogbackend.repository.CommentRepository;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

  @InjectMocks CommentService commentService;

  @Mock CommentRepository commentRepository;

  @Mock BlogPostRepository blogPostRepository;

  @Mock TimeProvider timeProvider;

  @Test
  void when_createComment_then_retrieveComment() {
    // Input comment
    CreateCommentDto createCommentDto = new CreateCommentDto("Test comment", 1L);

    Instant now = Instant.now();

    // Expected comment
    Comment comment =
        Comment.builder()
            .id(1L)
            .content("Test comment")
            .createdAt(now)
            .lastEditedAt(now)
            .isEdited(false)
            .build();

    BlogPost blogPost = new BlogPost();
    blogPost.setId(1L);
    blogPost.setTitle("Test title");
    blogPost.setContent("Test content");
    blogPost.setCreatedAt(now);
    blogPost.setComments(new HashSet<>());

    when(commentRepository.save(any())).thenReturn(comment);
    when(blogPostRepository.findById(1L)).thenReturn(Optional.of(blogPost));
    when(blogPostRepository.save(blogPost)).thenReturn(blogPost);
    when(timeProvider.getNow()).thenReturn(now);

    commentService.createComment(createCommentDto);

    verify(commentRepository, times(1)).save(any());
    verify(blogPostRepository, times(1)).findById(1L);
    verify(timeProvider, times(1)).getNow();
  }

  @Test
  void when_createComment_then_throwBlogPostNotFoundException() {
    // Input comment
    CreateCommentDto createCommentDto = new CreateCommentDto("Test comment", 1L);

    when(blogPostRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(
        BlogPostNotFoundException.class, () -> commentService.createComment(createCommentDto));
  }

  @Test
  void when_getCommentById_then_retrieveComment() {
    // Expected comment
    Comment comment =
        Comment.builder()
            .id(1L)
            .content("Test comment")
            .createdAt(Instant.now())
            .lastEditedAt(Instant.now())
            .isEdited(false)
            .build();

    when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

    CommentDto returnedComment = commentService.getCommentById(1L);

    verify(commentRepository, times(1)).findById(1L);

    Assertions.assertNotNull(returnedComment);
    Assertions.assertEquals(1L, returnedComment.id());
    Assertions.assertEquals("Test comment", returnedComment.content());
  }

  @Test
  void when_getCommentByInvalidId_then_throwCommentNotFoundException() {
    when(commentRepository.findById(1L)).thenReturn(Optional.empty());
    assertThrows(CommentNotFoundException.class, () -> commentService.getCommentById(1L));
    verify(commentRepository, times(1)).findById(1L);
  }

  @Test
  void when_getCommentsByBlogPostId_then_retrieveComments() {
    // Expected comments
    Comment comment1 =
        Comment.builder()
            .id(1L)
            .content("Test comment 1")
            .createdAt(Instant.now())
            .lastEditedAt(Instant.now())
            .isEdited(false)
            .build();

    Comment comment2 =
        Comment.builder()
            .id(2L)
            .content("Test comment 2")
            .createdAt(Instant.now())
            .lastEditedAt(Instant.now())
            .isEdited(false)
            .build();

    BlogPost blogPost = new BlogPost();
    blogPost.setId(1L);
    blogPost.setComments(new HashSet<>(Set.of(comment1, comment2)));

    when(blogPostRepository.findById(1L)).thenReturn(Optional.of(blogPost));
    when(commentRepository.findByBlogPostId(1L)).thenReturn(List.of(comment1, comment2));

    List<CommentDto> comments = commentService.getCommentsByBlogPostId(1L);

    verify(blogPostRepository, times(1)).findById(1L);

    Assertions.assertNotNull(comments);
    Assertions.assertEquals(2, comments.size());
  }

  @Test
  void when_getCommentsByInvalidBlogPostId_then_throwBlogPostNotFoundException() {
    when(blogPostRepository.findById(1L)).thenReturn(Optional.empty());
    assertThrows(BlogPostNotFoundException.class, () -> commentService.getCommentsByBlogPostId(1L));
    verify(blogPostRepository, times(1)).findById(1L);
  }
}
