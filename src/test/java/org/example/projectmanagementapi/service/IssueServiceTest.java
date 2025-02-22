package org.example.projectmanagementapi.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import org.example.projectmanagementapi.dto.IssueDto;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class IssueServiceTest {

  @Mock private IssueRepository issueRepository;

  @Mock private UserRepository userRepository;

  @Mock private ProjectRepository projectRepository;

  @Mock private NotificationService notificationService;

  @InjectMocks private IssueServiceImpl issueService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void createIssue_createsAndReturnsIssue() {
    IssueDto issueDto =
        IssueDto.builder()
            .title("Title")
            .description("Description")
            .reportedById(1)
            .assignedToId(1)
            .projectId(1)
            .priorityStatus(PriorityStatus.HIGH)
            .build();
    User reportedByUser = new User();
    User assignedToUser = new User();
    Project project = new Project();
    Issue issue = Issue.builder().id(1).title("Title").build();

    when(userRepository.findById(1)).thenReturn(Optional.of(reportedByUser));
    when(userRepository.findById(1)).thenReturn(Optional.of(assignedToUser));
    when(projectRepository.findById(1)).thenReturn(Optional.of(project));
    when(issueRepository.save(any(Issue.class))).thenReturn(issue);

    Issue createdIssue = issueService.createIssue(issueDto);

    assertNotNull(createdIssue);
    assertEquals("Title", createdIssue.getTitle());
    verify(issueRepository, times(1)).save(any(Issue.class));
    verify(notificationService, times(1)).createNotification(any(Notification.class));
  }

  @Test
  void createIssue_throwsExceptionWhenUserNotFound() {
    IssueDto issueDto =
        IssueDto.builder()
            .title("Title")
            .description("Description")
            .reportedById(1)
            .assignedToId(1)
            .projectId(1)
            .priorityStatus(PriorityStatus.HIGH)
            .build();

    when(userRepository.findById(1)).thenReturn(Optional.empty());

    assertThrows(RuntimeException.class, () -> issueService.createIssue(issueDto));
    verify(issueRepository, never()).save(any(Issue.class));
    verify(notificationService, never()).createNotification(any(Notification.class));
  }

  @Test
  void getIssue_returnsIssue() {
    Issue issue = Issue.builder().id(1).title("Title").build();
    when(issueRepository.findById(1)).thenReturn(Optional.of(issue));

    Issue result = issueService.getIssue(1);

    assertNotNull(result);
    assertEquals(issue, result);
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
    List<Issue> issues =
        List.of(
            Issue.builder().id(1).title("Title1").build(),
            Issue.builder().id(2).title("Title2").build());
    when(issueRepository.findAll()).thenReturn(issues);

    List<Issue> result = issueService.getAllIssues();

    assertEquals(issues.size(), result.size());
    verify(issueRepository, times(1)).findAll();
  }

  @Test
  void updateIssue_updatesAndReturnsIssue() {
    IssueDto issueDto =
        IssueDto.builder()
            .title("UpdatedTitle")
            .description("UpdatedDescription")
            .assignedToId(1)
            .projectId(1)
            .priorityStatus(PriorityStatus.LOW)
            .build();
    Issue issue = Issue.builder().id(1).title("Title").build();
    User assignedToUser = new User();

    when(issueRepository.findById(1)).thenReturn(Optional.of(issue));
    when(userRepository.findById(1)).thenReturn(Optional.of(assignedToUser));
    when(issueRepository.save(any(Issue.class))).thenReturn(issue);

    Issue updatedIssue = issueService.updateIssue(1, issueDto);

    assertNotNull(updatedIssue);
    assertEquals("UpdatedTitle", updatedIssue.getTitle());
    verify(issueRepository, times(1)).save(any(Issue.class));
    verify(notificationService, times(1)).createNotification(any(Notification.class));
  }

  @Test
  void updateIssue_throwsExceptionWhenIssueNotFound() {
    IssueDto issueDto =
        IssueDto.builder()
            .title("UpdatedTitle")
            .description("UpdatedDescription")
            .assignedToId(1)
            .projectId(1)
            .priorityStatus(PriorityStatus.LOW)
            .build();

    when(issueRepository.findById(1)).thenReturn(Optional.empty());

    assertThrows(RuntimeException.class, () -> issueService.updateIssue(1, issueDto));
    verify(issueRepository, never()).save(any(Issue.class));
    verify(notificationService, never()).createNotification(any(Notification.class));
  }

  @Test
  void deleteIssue_deletesIssue() {
    Issue issue = Issue.builder().id(1).title("Title").build();

    when(issueRepository.findById(1)).thenReturn(Optional.of(issue));

    issueService.deleteIssue(1);

    verify(issueRepository, times(1)).delete(issue);
    verify(notificationService, times(1)).createNotification(any(Notification.class));
  }

  @Test
  void deleteIssue_throwsExceptionWhenNotFound() {
    when(issueRepository.findById(1)).thenReturn(Optional.empty());

    assertThrows(RuntimeException.class, () -> issueService.deleteIssue(1));
    verify(issueRepository, times(1)).findById(1);
    verify(issueRepository, never()).delete(any(Issue.class));
    verify(notificationService, never()).createNotification(any(Notification.class));
  }
}
