package org.example.projectmanagementapi.service;

import java.util.List;
import org.example.projectmanagementapi.dto.CommentDto;
import org.example.projectmanagementapi.entity.Comment;

public interface CommentService {
  Comment createComment(CommentDto comment);

  Comment getComment(Integer commentId);

  List<Comment> getComments();

  Comment updateComment(Integer commentId, CommentDto comment);

  void deleteComment(Integer commentId);
}
