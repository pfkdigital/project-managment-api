package org.example.projectmanagementapi.service.impl;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.Cacheable;
import lombok.RequiredArgsConstructor;
import org.example.projectmanagementapi.dto.request.CommentRequestDto;
import org.example.projectmanagementapi.dto.request.CommentUpdateRequest;
import org.example.projectmanagementapi.dto.response.CommentDto;
import org.example.projectmanagementapi.entity.*;
import org.example.projectmanagementapi.enums.NotificationType;
import org.example.projectmanagementapi.mapper.CommentMapper;
import org.example.projectmanagementapi.repository.CommentRepository;
import org.example.projectmanagementapi.repository.IssueRepository;
import org.example.projectmanagementapi.repository.TaskRepository;
import org.example.projectmanagementapi.repository.UserRepository;
import org.example.projectmanagementapi.service.CommentService;
import org.example.projectmanagementapi.service.NotificationService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
//import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

  private final CommentMapper commentMapper;
  private final CommentRepository commentRepository;
  private final IssueRepository issueRepository;
  private final TaskRepository taskRepository;
  private final UserRepository userRepository;
  private final NotificationService notificationService;

  @Override
  public CommentDto createComment(CommentRequestDto comment) {
    User author = findUserById(comment.getAuthorId());

    Comment newComment =
        Comment.builder()
            .content(comment.getContent())
            .createdAt(LocalDate.now())
            .author(author)
            .isEdited(false)
            .build();

    author.addComment(newComment);

    if (comment.getIssueId() != null) {
      Issue issue = findIssueById(comment.getIssueId());
      issue.addComment(newComment);
    }

    if (comment.getTaskId() != null) {
      Task task = findTaskById(comment.getTaskId());
      task.addComment(newComment);
    }

    Comment savedComment = commentRepository.save(newComment);

    notificationService.createNotification(
        "Comment " + savedComment.getId() + " has been created", NotificationType.CREATION);

    return commentMapper.toDto(savedComment);
  }

  @Override
  @CachePut(value = "comments", key = "#commentId")
  public CommentDto getComment(Integer commentId) {
    return commentMapper.toDto(findCommentById(commentId));
  }

  @Override
  public List<CommentDto> getComments() {
    return commentRepository.findAll().stream().map(commentMapper::toDto).toList();
  }

  @Override
  @CachePut(value = "comments", key = "#commentId")
  public CommentDto updateComment(Integer commentId, CommentUpdateRequest comment) {

    //    System.out.println(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    //    User currentUser = (User)
    // SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    //
    //    if (currentUser == null) {
    //      throw new IllegalArgumentException("User not found");
    //    }
    //
    //    if (!currentUser.getId().equals(comment.getAuthorId())) {
    //      throw new IllegalArgumentException("You can only update your own comments");
    //    }

    Comment existingComment = findCommentById(commentId);
    existingComment.setContent(comment.getContent());
    existingComment.setUpdatedAt(LocalDate.now());
    existingComment.setIsEdited(true);

    Comment updatedComment = commentRepository.save(existingComment);

    notificationService.createNotification(
        "Comment " + existingComment.getId() + " has been updated", NotificationType.UPDATE);

    return commentMapper.toDto(updatedComment);
  }

  @Override
  @CacheEvict(value = "comments", key = "#commentId")
  public void deleteComment(Integer commentId) {
    Comment comment = findCommentById(commentId);
    commentRepository.delete(comment);

    notificationService.createNotification(
        "Comment " + commentId + " has been deleted", NotificationType.DESTRUCTION);
  }

  private User findUserById(Integer userId) {
    return userRepository
        .findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("User with id " + userId + " not found"));
  }

  private Comment findCommentById(Integer commentId) {
    return commentRepository
        .findById(commentId)
        .orElseThrow(
            () -> new IllegalArgumentException("Comment with id " + commentId + " not found"));
  }

  private Issue findIssueById(Integer issueId) {
    return issueRepository.findById(issueId).orElse(null);
  }

  private Task findTaskById(Integer taskId) {
    return taskRepository.findById(taskId).orElse(null);
  }
}
