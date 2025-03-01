package org.example.projectmanagementapi.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import org.example.projectmanagementapi.dto.request.IssueRequestDto;
import org.example.projectmanagementapi.dto.response.DetailedIssueDto;
import org.example.projectmanagementapi.dto.response.IssueDto;
import org.example.projectmanagementapi.entity.Issue;
import org.example.projectmanagementapi.entity.Notification;
import org.example.projectmanagementapi.entity.Project;
import org.example.projectmanagementapi.entity.User;
import org.example.projectmanagementapi.enums.PriorityStatus;
import org.example.projectmanagementapi.repository.IssueRepository;
import org.example.projectmanagementapi.repository.ProjectRepository;
import org.example.projectmanagementapi.repository.UserRepository;
import org.example.projectmanagementapi.service.impl.IssueServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class IssueServiceTest {

  @Mock private IssueRepository issueRepository;
  @Mock private UserRepository userRepository;
  @Mock private ProjectRepository projectRepository;
  @Mock private NotificationService notificationService;

  @InjectMocks private IssueServiceImpl issueService;

  private Issue issue;
  private User reportedByUser;
  private User assignedToUser;
  private Project project;
  private IssueRequestDto issueRequestDto;

  @BeforeEach
  void setUp() {
    reportedByUser = new User();
    assignedToUser = new User();
    project = new Project();
    issue = Issue.builder().id(1).title("Title").build();

    issueRequestDto = IssueRequestDto.builder()
            .title("Title")
            .description("Description")
            .reportedById(1)
            .assignedToId(1)
            .projectId(1)
            .priorityStatus(PriorityStatus.HIGH)
            .build();
  }

  @Test
  void createIssue_createsAndReturnsIssue() {
    when(userRepository.findById(1)).thenReturn(Optional.of(reportedByUser));
    when(userRepository.findById(1)).thenReturn(Optional.of(assignedToUser));
    when(projectRepository.findById(1)).thenReturn(Optional.of(project));
    when(issueRepository.save(any(Issue.class))).thenReturn(issue);

    IssueDto createdIssue = issueService.createIssue(issueRequestDto);

    assertNotNull(createdIssue);
    assertEquals("Title", createdIssue.getTitle());
    verify(issueRepository, times(1)).save(any(Issue.class));
    verify(notificationService, times(1)).createNotification(any(Notification.class));
  }

  @Test
  void createIssue_throwsExceptionWhenUserNotFound() {
    when(userRepository.findById(1)).thenReturn(Optional.empty());

    assertThrows(RuntimeException.class, () -> issueService.createIssue(issueRequestDto));
    verify(issueRepository, never()).save(any(Issue.class));
    verify(notificationService, never()).createNotification(any(Notification.class));
  }

  @Test
  void getIssue_returnsIssue() {
    when(issueRepository.findById(1)).thenReturn(Optional.of(issue));

    DetailedIssueDto result = issueService.getIssue(1);

    assertNotNull(result);
    assertEquals(issue.getTitle(), result.getTitle());
    verify(issueRepository, times(1)).findById(1);
  }

  @Test
  void getIssue_throwsExceptionWhenNotFound() {
    when(issueRepository.findById(1)).thenReturn(Optional.empty());

    assertThrows(RuntimeException.class, () -> issueService.getIssue(1));
    verify(issueRepository, times(1)).findById(1);
  }

  @Test
  void getAllIssues_returnsAllIssues() {
    List<Issue> issues = List.of(
            Issue.builder().id(1).title("Title1").build(),
            Issue.builder().id(2).title("Title2").build());
    when(issueRepository.findAll()).thenReturn(issues);

    List<IssueDto> result = issueService.getAllIssues();

    assertEquals(issues.size(), result.size());
    verify(issueRepository, times(1)).findAll();
  }

  @Test
  void updateIssue_updatesAndReturnsIssue() {
    when(issueRepository.findById(1)).thenReturn(Optional.of(issue));
    when(userRepository.findById(1)).thenReturn(Optional.of(assignedToUser));
    when(issueRepository.save(any(Issue.class))).thenReturn(issue);

    DetailedIssueDto updatedIssue = issueService.updateIssue(1, issueRequestDto);

    assertNotNull(updatedIssue);
    assertEquals("Title", updatedIssue.getTitle());
    verify(issueRepository, times(1)).save(any(Issue.class));
    verify(notificationService, times(1)).createNotification(any(Notification.class));
  }

  @Test
  void updateIssue_throwsExceptionWhenIssueNotFound() {
    when(issueRepository.findById(1)).thenReturn(Optional.empty());

    assertThrows(RuntimeException.class, () -> issueService.updateIssue(1, issueRequestDto));
    verify(issueRepository, never()).save(any(Issue.class));
    verify(notificationService, never()).createNotification(any(Notification.class));
  }

  @Test
  void deleteIssue_deletesIssue() {
    when(issueRepository.findById(1)).thenReturn(Optional.of(issue));

    issueService.deleteIssue(1);

    verify(issueRepository, times(1)).delete(issue);
    verify(notificationService, times(1)).createNotification(any(Notification.class));
  }

  @Test
  void deleteIssue_throwsExceptionWhenNotFound() {
    when(issueRepository.findById(1)).thenReturn(Optional.empty());

    assertThrows(RuntimeException.class, () -> issueService.deleteIssue(1));
    verify(issueRepository, never()).delete(any(Issue.class));
    verify(notificationService, never()).createNotification(any(Notification.class));
  }
}