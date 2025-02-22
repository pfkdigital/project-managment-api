package org.example.projectmanagementapi.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import org.example.projectmanagementapi.dto.CommentDto;
import org.example.projectmanagementapi.entity.Comment;
import org.example.projectmanagementapi.entity.Issue;
import org.example.projectmanagementapi.entity.Task;
import org.example.projectmanagementapi.entity.User;
import org.example.projectmanagementapi.repository.CommentRepository;
import org.example.projectmanagementapi.repository.IssueRepository;
import org.example.projectmanagementapi.repository.TaskRepository;
import org.example.projectmanagementapi.repository.UserRepository;
import org.example.projectmanagementapi.service.impl.CommentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class CommentServiceTest {

  @Mock private CommentRepository commentRepository;

  @Mock private IssueRepository issueRepository;

  @Mock private TaskRepository taskRepository;

  @Mock private UserRepository userRepository;

  @Mock private NotificationService notificationService;

  @InjectMocks private CommentServiceImpl commentService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void createComment_createsAndReturnsComment() {
    CommentDto commentDto =
        CommentDto.builder().content("Content").authorId(1).issueId(1).taskId(1).build();
    User author = new User();
    Issue issue = new Issue();
    Task task = new Task();
    Comment comment = Comment.builder().id(1).content("Content").build();

    when(userRepository.findById(1)).thenReturn(Optional.of(author));
    when(issueRepository.findById(1)).thenReturn(Optional.of(issue));
    when(taskRepository.findById(1)).thenReturn(Optional.of(task));
    when(commentRepository.save(any(Comment.class))).thenReturn(comment);

    Comment createdComment = commentService.createComment(commentDto);

    assertNotNull(createdComment);
    assertEquals("Content", createdComment.getContent());
    verify(commentRepository, times(1)).save(any(Comment.class));
    verify(notificationService, times(1)).createNotification(any());
  }

  @Test
  void createComment_throwsExceptionWhenUserNotFound() {
    CommentDto commentDto =
        CommentDto.builder().content("Content").authorId(1).issueId(1).taskId(1).build();

    when(userRepository.findById(1)).thenReturn(Optional.empty());

    assertThrows(IllegalArgumentException.class, () -> commentService.createComment(commentDto));
    verify(commentRepository, never()).save(any(Comment.class));
    verify(notificationService, never()).createNotification(any());
  }

  @Test
  void getComment_returnsComment() {
    Comment comment = Comment.builder().id(1).content("Content").build();
    when(commentRepository.findById(1)).thenReturn(Optional.of(comment));

    Comment result = commentService.getComment(1);

    assertNotNull(result);
    assertEquals(comment, result);
    verify(commentRepository, times(1)).findById(1);
  }

  @Test
  void getComment_throwsExceptionWhenNotFound() {
    when(commentRepository.findById(1)).thenReturn(Optional.empty());

    assertThrows(IllegalArgumentException.class, () -> commentService.getComment(1));
    verify(commentRepository, times(1)).findById(1);
  }

  @Test
  void getComments_returnsAllComments() {
    List<Comment> comments =
        List.of(
            Comment.builder().id(1).content("Content1").build(),
            Comment.builder().id(2).content("Content2").build());
    when(commentRepository.findAll()).thenReturn(comments);

    List<Comment> result = commentService.getComments();

    assertEquals(comments.size(), result.size());
    verify(commentRepository, times(1)).findAll();
  }

  @Test
  void updateComment_updatesAndReturnsComment() {
    CommentDto commentDto = CommentDto.builder().content("UpdatedContent").build();
    Comment comment = Comment.builder().id(1).content("Content").build();

    when(commentRepository.findById(1)).thenReturn(Optional.of(comment));
    when(commentRepository.save(any(Comment.class))).thenReturn(comment);

    Comment updatedComment = commentService.updateComment(1, commentDto);

    assertNotNull(updatedComment);
    assertEquals("UpdatedContent", updatedComment.getContent());
    verify(commentRepository, times(1)).save(any(Comment.class));
    verify(notificationService, times(1)).createNotification(any());
  }

  @Test
  void updateComment_throwsExceptionWhenCommentNotFound() {
    CommentDto commentDto = CommentDto.builder().content("UpdatedContent").build();

    when(commentRepository.findById(1)).thenReturn(Optional.empty());

    assertThrows(IllegalArgumentException.class, () -> commentService.updateComment(1, commentDto));
    verify(commentRepository, never()).save(any(Comment.class));
    verify(notificationService, never()).createNotification(any());
  }

  @Test
  void deleteComment_deletesComment() {
    Comment comment = Comment.builder().id(1).content("Content").build();

    when(commentRepository.findById(1)).thenReturn(Optional.of(comment));

    commentService.deleteComment(1);

    verify(commentRepository, times(1)).delete(comment);
    verify(notificationService, times(1)).createNotification(any());
  }

  @Test
  void deleteComment_throwsExceptionWhenNotFound() {
    when(commentRepository.findById(1)).thenReturn(Optional.empty());

    assertThrows(IllegalArgumentException.class, () -> commentService.deleteComment(1));
    verify(commentRepository, times(1)).findById(1);
    verify(commentRepository, never()).delete(any(Comment.class));
    verify(notificationService, never()).createNotification(any());
  }
}
