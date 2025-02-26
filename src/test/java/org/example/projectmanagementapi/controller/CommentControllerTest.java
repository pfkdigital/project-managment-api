package org.example.projectmanagementapi.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Collections;
import org.example.projectmanagementapi.dto.request.CommentRequestDto;
import org.example.projectmanagementapi.entity.Comment;
import org.example.projectmanagementapi.service.impl.CommentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class CommentControllerTest {

  @Mock private CommentServiceImpl commentService;

  @InjectMocks private CommentController commentController;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testCreateComment() {
    Comment comment = new Comment();
    when(commentService.createComment(any(CommentRequestDto.class))).thenReturn(comment);

    ResponseEntity<?> response = commentController.createComment(new CommentRequestDto());

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(comment, response.getBody());
  }

  @Test
  void testGetComments() {
    when(commentService.getComments()).thenReturn(Collections.emptyList());

    ResponseEntity<?> response = commentController.getComments();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(Collections.emptyList(), response.getBody());
  }

  @Test
  void testGetComment() {
    Comment comment = new Comment();
    when(commentService.getComment(anyInt())).thenReturn(comment);

    ResponseEntity<?> response = commentController.getComment(1);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(comment, response.getBody());
  }

  @Test
  void testUpdateComment() {
    Comment comment = new Comment();
    when(commentService.updateComment(anyInt(), any(CommentRequestDto.class))).thenReturn(comment);

    ResponseEntity<?> response = commentController.updateComment(1, new CommentRequestDto());

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(comment, response.getBody());
  }

  @Test
  void testDeleteComment() {
    doNothing().when(commentService).deleteComment(anyInt());

    ResponseEntity<?> response = commentController.deleteComment(1);

    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    verify(commentService, times(1)).deleteComment(1);
  }
}
