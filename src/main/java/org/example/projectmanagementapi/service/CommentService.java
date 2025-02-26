package org.example.projectmanagementapi.service;

import java.util.List;
import org.example.projectmanagementapi.dto.request.CommentRequestDto;
import org.example.projectmanagementapi.dto.response.CommentDto;

public interface CommentService {
  CommentDto createComment(CommentRequestDto comment);

  CommentDto getComment(Integer commentId);

  List<CommentDto> getComments();

  CommentDto updateComment(Integer commentId, CommentRequestDto comment);

  void deleteComment(Integer commentId);
}
