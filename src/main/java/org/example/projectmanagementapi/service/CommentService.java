package org.example.projectmanagementapi.service;

import org.example.projectmanagementapi.entity.Comment;
import org.example.projectmanagementapi.dto.CommentDto;

import java.util.List;

public interface CommentService {
    Comment createComment(CommentDto comment);
    Comment getComment(Integer commentId);
    List<Comment> getComments();
    Comment updateComment(Integer commentId, CommentDto comment);
    void deleteComment(Integer commentId);
}
