package org.example.projectmanagementapi.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.projectmanagementapi.annotation.RateLimitedRestController;
import org.example.projectmanagementapi.dto.request.CommentRequestDto;
import org.example.projectmanagementapi.dto.request.CommentUpdateRequest;
import org.example.projectmanagementapi.dto.response.CommentDto;
import org.example.projectmanagementapi.service.impl.CommentServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RateLimitedRestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
public class CommentController {

  private final CommentServiceImpl commentService;

  @PostMapping
  public ResponseEntity<CommentDto> createComment(@Valid @RequestBody CommentRequestDto comment) {
    return new ResponseEntity<>(commentService.createComment(comment), HttpStatus.CREATED);
  }

  @GetMapping
  public ResponseEntity<List<CommentDto>> getComments() {
    return new ResponseEntity<>(commentService.getComments(), HttpStatus.OK);
  }

  @GetMapping("/{commentId}")
  public ResponseEntity<CommentDto> getComment(@PathVariable Integer commentId) {
    return new ResponseEntity<>(commentService.getComment(commentId), HttpStatus.OK);
  }

  @PutMapping("/{commentId}")
  public ResponseEntity<CommentDto> updateComment(
      @PathVariable Integer commentId, @Valid @RequestBody CommentUpdateRequest comment) {
    return new ResponseEntity<>(commentService.updateComment(commentId, comment), HttpStatus.OK);
  }

  @DeleteMapping("/{commentId}")
  public ResponseEntity<?> deleteComment(@PathVariable Integer commentId) {
    commentService.deleteComment(commentId);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
