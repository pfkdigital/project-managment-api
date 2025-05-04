package org.example.projectmanagementapi.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.example.projectmanagementapi.dto.request.CommentRequestDto;
import org.example.projectmanagementapi.dto.request.CommentUpdateRequest;
import org.example.projectmanagementapi.dto.response.CommentDto;
import org.example.projectmanagementapi.entity.Comment;
import org.example.projectmanagementapi.entity.Issue;
import org.example.projectmanagementapi.entity.Task;
import org.example.projectmanagementapi.entity.User;
import org.example.projectmanagementapi.enums.NotificationType;
import org.example.projectmanagementapi.mapper.CommentMapper;
import org.example.projectmanagementapi.repository.CommentRepository;
import org.example.projectmanagementapi.repository.IssueRepository;
import org.example.projectmanagementapi.repository.TaskRepository;
import org.example.projectmanagementapi.repository.UserRepository;
import org.example.projectmanagementapi.service.impl.CommentServiceImpl;
import org.example.projectmanagementapi.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

  @Mock private CommentRepository commentRepository;
  @Mock private IssueRepository issueRepository;
  @Mock private TaskRepository taskRepository;
  @Mock private UserRepository userRepository;
  @Mock private NotificationService notificationService;
  @Mock private CommentMapper commentMapper;

  @InjectMocks private CommentServiceImpl commentService;

  private Comment comment;
  private Issue issue;
  private Task task;
  private User user;
  private CommentRequestDto commentRequestDto;
  private CommentUpdateRequest commentUpdateRequest;
  private CommentDto commentDto;

  @BeforeEach
  void setUp() {
    // Initialize User
    user = new User();
    user.setId(1);
    user.setUsername("testUser");
    user.setComments(new ArrayList<>());

    // Initialize Issue
    issue = new Issue();
    issue.setId(1);
    issue.setTitle("Test Issue");
    issue.setComments(new ArrayList<>());

    // Initialize Task
    task = new Task();
    task.setId(1);
    task.setDescription("Test Task");
    task.setComments(new ArrayList<>());

    // Initialize Comment
    comment = Comment.builder()
            .id(1)
            .content("Test Comment")
            .createdAt(LocalDate.now())
            .author(user)
            .isEdited(false)
            .build();

    // Initialize CommentRequestDto
    commentRequestDto = new CommentRequestDto();
    commentRequestDto.setContent("Test Comment");
    commentRequestDto.setAuthorId(1);

    // Initialize CommentUpdateRequest
    commentUpdateRequest = new CommentUpdateRequest();
    commentUpdateRequest.setContent("Updated Comment");

    // Initialize CommentDto
    commentDto = new CommentDto();
    commentDto.setId(1);
    commentDto.setContent("Test Comment");
  }

  @Test
  void createComment_withIssue_createsAndReturnsComment() {
    // Arrange
    commentRequestDto.setIssueId(1);

    when(userRepository.findById(1)).thenReturn(Optional.of(user));
    when(issueRepository.findById(1)).thenReturn(Optional.of(issue));
    when(commentRepository.save(any(Comment.class))).thenReturn(comment);
    when(commentMapper.toDto(any(Comment.class))).thenReturn(commentDto);

    // Act
    CommentDto result = commentService.createComment(commentRequestDto);

    // Assert
    assertNotNull(result);
    assertEquals(commentDto.getId(), result.getId());
    assertEquals(commentDto.getContent(), result.getContent());

    verify(userRepository).findById(1);
    verify(issueRepository).findById(1);
    verify(commentRepository).save(any(Comment.class));
    verify(notificationService).createNotification(anyString(), eq(NotificationType.CREATION));
    verify(commentMapper).toDto(any(Comment.class));
  }

  @Test
  void createComment_withTask_createsAndReturnsComment() {
    // Arrange
    commentRequestDto.setTaskId(1);

    when(userRepository.findById(1)).thenReturn(Optional.of(user));
    when(taskRepository.findById(1)).thenReturn(Optional.of(task));
    when(commentRepository.save(any(Comment.class))).thenReturn(comment);
    when(commentMapper.toDto(any(Comment.class))).thenReturn(commentDto);

    // Act
    CommentDto result = commentService.createComment(commentRequestDto);

    // Assert
    assertNotNull(result);
    assertEquals(commentDto.getId(), result.getId());
    assertEquals(commentDto.getContent(), result.getContent());

    verify(userRepository).findById(1);
    verify(taskRepository).findById(1);
    verify(commentRepository).save(any(Comment.class));
    verify(notificationService).createNotification(anyString(), eq(NotificationType.CREATION));
    verify(commentMapper).toDto(any(Comment.class));
  }

  @Test
  void createComment_withBothIssueAndTask_createsAndReturnsComment() {
    // Arrange
    commentRequestDto.setIssueId(1);
    commentRequestDto.setTaskId(1);

    when(userRepository.findById(1)).thenReturn(Optional.of(user));
    when(issueRepository.findById(1)).thenReturn(Optional.of(issue));
    when(taskRepository.findById(1)).thenReturn(Optional.of(task));
    when(commentRepository.save(any(Comment.class))).thenReturn(comment);
    when(commentMapper.toDto(any(Comment.class))).thenReturn(commentDto);

    // Act
    CommentDto result = commentService.createComment(commentRequestDto);

    // Assert
    assertNotNull(result);
    assertEquals(commentDto.getId(), result.getId());
    assertEquals(commentDto.getContent(), result.getContent());

    verify(userRepository).findById(1);
    verify(issueRepository).findById(1);
    verify(taskRepository).findById(1);
    verify(commentRepository).save(any(Comment.class));
    verify(notificationService).createNotification(anyString(), eq(NotificationType.CREATION));
    verify(commentMapper).toDto(any(Comment.class));
  }

  @Test
  void createComment_throwsExceptionWhenUserNotFound() {
    // Arrange
    when(userRepository.findById(1)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(IllegalArgumentException.class, () -> commentService.createComment(commentRequestDto));
    verify(userRepository).findById(1);
    verify(commentRepository, never()).save(any(Comment.class));
    verify(notificationService, never()).createNotification(anyString(), any(NotificationType.class));
  }

  @Test
  void getComment_returnsComment() {
    // Arrange
    when(commentRepository.findById(1)).thenReturn(Optional.of(comment));
    when(commentMapper.toDto(comment)).thenReturn(commentDto);

    // Act
    CommentDto result = commentService.getComment(1);

    // Assert
    assertNotNull(result);
    assertEquals(commentDto.getId(), result.getId());
    assertEquals(commentDto.getContent(), result.getContent());
    verify(commentRepository).findById(1);
    verify(commentMapper).toDto(comment);
  }

  @Test
  void getComment_throwsExceptionWhenNotFound() {
    // Arrange
    when(commentRepository.findById(1)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(IllegalArgumentException.class, () -> commentService.getComment(1));
    verify(commentRepository).findById(1);
    verify(commentMapper, never()).toDto(any(Comment.class));
  }

  @Test
  void getComments_returnsAllComments() {
    // Arrange
    List<Comment> comments = List.of(comment);
    when(commentRepository.findAll()).thenReturn(comments);
    when(commentMapper.toDto(comment)).thenReturn(commentDto);

    // Act
    List<CommentDto> results = commentService.getComments();

    // Assert
    assertNotNull(results);
    assertEquals(1, results.size());
    assertEquals(commentDto.getId(), results.get(0).getId());
    assertEquals(commentDto.getContent(), results.get(0).getContent());
    verify(commentRepository).findAll();
    verify(commentMapper, times(comments.size())).toDto(any(Comment.class));
  }

  @Test
  void updateComment_updatesAndReturnsComment() {
    // Arrange
    when(commentRepository.findById(1)).thenReturn(Optional.of(comment));
    when(commentRepository.save(any(Comment.class))).thenReturn(comment);

    CommentDto updatedCommentDto = new CommentDto();
    updatedCommentDto.setId(1);
    updatedCommentDto.setContent("Updated Comment");

    when(commentMapper.toDto(any(Comment.class))).thenReturn(updatedCommentDto);

    // Act
    CommentDto result = commentService.updateComment(1, commentUpdateRequest);

    // Assert
    assertNotNull(result);
    assertEquals(updatedCommentDto.getId(), result.getId());
    assertEquals(updatedCommentDto.getContent(), result.getContent());

    verify(commentRepository).findById(1);
    verify(commentRepository).save(comment);
    verify(notificationService).createNotification(anyString(), eq(NotificationType.UPDATE));
    verify(commentMapper).toDto(comment);
  }

  @Test
  void updateComment_throwsExceptionWhenNotFound() {
    // Arrange
    when(commentRepository.findById(1)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(IllegalArgumentException.class, () -> commentService.updateComment(1, commentUpdateRequest));

    verify(commentRepository).findById(1);
    verify(commentRepository, never()).save(any(Comment.class));
    verify(notificationService, never()).createNotification(anyString(), any(NotificationType.class));
  }

  @Test
  void deleteComment_deletesComment() {
    // Arrange
    when(commentRepository.findById(1)).thenReturn(Optional.of(comment));

    // Act
    commentService.deleteComment(1);

    // Assert
    verify(commentRepository).findById(1);
    verify(commentRepository).delete(comment);
    verify(notificationService).createNotification(anyString(), eq(NotificationType.DESTRUCTION));
  }

  @Test
  void deleteComment_throwsExceptionWhenNotFound() {
    // Arrange
    when(commentRepository.findById(1)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(IllegalArgumentException.class, () -> commentService.deleteComment(1));

    verify(commentRepository).findById(1);
    verify(commentRepository, never()).delete(any(Comment.class));
    verify(notificationService, never()).createNotification(anyString(), any(NotificationType.class));
  }
}