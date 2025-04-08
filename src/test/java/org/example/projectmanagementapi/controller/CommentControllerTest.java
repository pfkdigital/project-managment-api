package org.example.projectmanagementapi.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;
import org.example.projectmanagementapi.dto.request.CommentRequestDto;
import org.example.projectmanagementapi.dto.request.CommentUpdateRequest;
import org.example.projectmanagementapi.dto.response.CommentDto;
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
    CommentDto commentDto = new CommentDto();
    when(commentService.createComment(any(CommentRequestDto.class))).thenReturn(commentDto);

    ResponseEntity<?> response = commentController.createComment(new CommentRequestDto());

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(commentDto, response.getBody());
  }

  @Test
  void testGetComments() {
    List<CommentDto> comments = Collections.emptyList();
    when(commentService.getComments()).thenReturn(comments);

    ResponseEntity<?> response = commentController.getComments();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(comments, response.getBody());
  }

  @Test
  void testGetComment() {
    CommentDto commentDto = new CommentDto();
    when(commentService.getComment(anyInt())).thenReturn(commentDto);

    ResponseEntity<?> response = commentController.getComment(1);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(commentDto, response.getBody());
  }

  @Test
  void testUpdateComment() {
    CommentDto commentDto = new CommentDto();
    when(commentService.updateComment(anyInt(), any(CommentUpdateRequest.class)))
        .thenReturn(commentDto);

    ResponseEntity<?> response = commentController.updateComment(1, new CommentUpdateRequest());

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(commentDto, response.getBody());
  }

  @Test
  void testDeleteComment() {
    doNothing().when(commentService).deleteComment(anyInt());

    ResponseEntity<?> response = commentController.deleteComment(1);

    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    verify(commentService, times(1)).deleteComment(1);
  }
}
