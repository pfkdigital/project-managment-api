package org.example.projectmanagementapi.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import org.example.projectmanagementapi.dto.request.CommentRequestDto;
import org.example.projectmanagementapi.dto.response.CommentDto;
import org.example.projectmanagementapi.entity.Comment;
import org.example.projectmanagementapi.entity.Issue;
import org.example.projectmanagementapi.entity.User;
import org.example.projectmanagementapi.repository.CommentRepository;
import org.example.projectmanagementapi.repository.IssueRepository;
import org.example.projectmanagementapi.repository.UserRepository;
import org.example.projectmanagementapi.service.impl.CommentServiceImpl;
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
  @Mock private UserRepository userRepository;

  @InjectMocks private CommentServiceImpl commentService;

  private Comment comment;
  private Issue issue;
  private User user;
  private CommentRequestDto commentRequestDto;

  @BeforeEach
  void setUp() {
    user = new User();
    issue = new Issue();
    comment = new Comment();
    comment.setId(1);
    comment.setContent("Test Comment");

    commentRequestDto = new CommentRequestDto();
    commentRequestDto.setContent("Test Comment");
    commentRequestDto.setIssueId(1);
    commentRequestDto.setAuthorId(1);
  }

  @Test
  void createComment_createsAndReturnsComment() {
    when(userRepository.findById(1)).thenReturn(Optional.of(user));
    when(issueRepository.findById(1)).thenReturn(Optional.of(issue));
    when(commentRepository.save(any(Comment.class))).thenReturn(comment);

    CommentDto createdComment = commentService.createComment(commentRequestDto);

    assertNotNull(createdComment);
    assertEquals("Test Comment", createdComment.getContent());
    verify(commentRepository, times(1)).save(any(Comment.class));
  }

  @Test
  void createComment_throwsExceptionWhenUserNotFound() {
    when(userRepository.findById(1)).thenReturn(Optional.empty());

    assertThrows(RuntimeException.class, () -> commentService.createComment(commentRequestDto));
    verify(commentRepository, never()).save(any(Comment.class));
  }

  @Test
  void getComment_returnsComment() {
    when(commentRepository.findById(1)).thenReturn(Optional.of(comment));

    CommentDto result = commentService.getComment(1);

    assertNotNull(result);
    assertEquals(comment.getContent(), result.getContent());
    verify(commentRepository, times(1)).findById(1);
  }

  @Test
  void getComment_throwsExceptionWhenNotFound() {
    when(commentRepository.findById(1)).thenReturn(Optional.empty());

    assertThrows(RuntimeException.class, () -> commentService.getComment(1));
    verify(commentRepository, times(1)).findById(1);
  }

  @Test
  void deleteComment_deletesComment() {
    when(commentRepository.findById(1)).thenReturn(Optional.of(comment));

    commentService.deleteComment(1);

    verify(commentRepository, times(1)).delete(comment);
  }

  @Test
  void deleteComment_throwsExceptionWhenNotFound() {
    when(commentRepository.findById(1)).thenReturn(Optional.empty());

    assertThrows(RuntimeException.class, () -> commentService.deleteComment(1));
    verify(commentRepository, never()).delete(any(Comment.class));
  }
}