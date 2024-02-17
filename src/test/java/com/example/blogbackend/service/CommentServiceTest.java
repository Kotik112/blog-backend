package com.example.blogbackend.service;

import com.example.blogbackend.dto.CommentDto;
import com.example.blogbackend.dto.CreateCommentDto;
import com.example.blogbackend.provider.TimeProvider;
import com.example.blogbackend.repository.CommentRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CommentServiceTest {

    @InjectMocks
    CommentService commentService;

    @Mock
    CommentRepository commentRepository;

    @Mock
    TimeProvider timeProvider;

    @Test
    public void when_createComment_then_retrieveComment() {
        // Input comment
        CreateCommentDto createCommentDto = new CreateCommentDto("Test comment", 1L);

        // Expected comment
        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .content("Test comment")
                .createdAt(Instant.parse("2023-04-04T12:42:00Z"))
                .isEdited(false)
                .build();

/*        Comment returnedComment = new Comment(
                1L,
                "Test comment",
                Instant.parse("2023-04-04T12:42:00Z"),
                null,
                false
                );*/

        when(commentRepository.save(any())).thenReturn(commentDto);
        commentService.createComment(createCommentDto);
    }
}
