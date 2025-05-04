package org.example.projectmanagementapi.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.example.projectmanagementapi.dto.request.IssueRequestDto;
import org.example.projectmanagementapi.dto.response.DetailedIssueDto;
import org.example.projectmanagementapi.dto.response.IssueDto;
import org.example.projectmanagementapi.entity.Issue;
import org.example.projectmanagementapi.entity.Notification;
import org.example.projectmanagementapi.entity.Project;
import org.example.projectmanagementapi.entity.User;
import org.example.projectmanagementapi.enums.IssueStatus;
import org.example.projectmanagementapi.enums.NotificationType;
import org.example.projectmanagementapi.enums.PriorityStatus;
import org.example.projectmanagementapi.mapper.IssueMapper;
import org.example.projectmanagementapi.repository.IssueRepository;
import org.example.projectmanagementapi.repository.ProjectRepository;
import org.example.projectmanagementapi.repository.UserRepository;
import org.example.projectmanagementapi.service.NotificationService;
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
  @Mock private IssueMapper issueMapper;

  @InjectMocks private IssueServiceImpl issueService;

  private Issue issue;
  private User reportedByUser;
  private User assignedToUser;
  private Project project;
  private IssueRequestDto issueRequestDto;
  private IssueDto issueDto;
  private DetailedIssueDto detailedIssueDto;

  @BeforeEach
  void setUp() {
    // Initialize User objects
    reportedByUser = new User();
    reportedByUser.setId(1);
    reportedByUser.setUsername("reporter");
    reportedByUser.setReportedIssues(new ArrayList<>());
    reportedByUser.setAssignedIssues(new ArrayList<>());

    assignedToUser = new User();
    assignedToUser.setId(2);
    assignedToUser.setUsername("assignee");
    assignedToUser.setReportedIssues(new ArrayList<>());
    assignedToUser.setAssignedIssues(new ArrayList<>());

    // Initialize Project object
    project = new Project();
    project.setId(1);
    project.setName("Test Project");
    project.setIssues(new ArrayList<>());

    // Initialize Issue object
    issue =
        Issue.builder()
            .id(1)
            .title("Test Issue")
            .description("Test Description")
            .priorityStatus(PriorityStatus.HIGH)
            .status(IssueStatus.OPEN)
            .reportedBy(reportedByUser)
            .assignedTo(assignedToUser)
            .build();
    issue.setComments(new ArrayList<>());
    issue.setAttachments(new ArrayList<>());

    // Initialize IssueRequestDto
    issueRequestDto =
        IssueRequestDto.builder()
            .title("Test Issue")
            .description("Test Description")
            .reportedById(1)
            .assignedToId(2)
            .projectId(1)
            .priorityStatus(PriorityStatus.HIGH)
            .status(IssueStatus.OPEN)
            .build();

    // Initialize IssueDto
    issueDto = new IssueDto();
    issueDto.setId(1);
    issueDto.setTitle("Test Issue");

    // Initialize DetailedIssueDto
    detailedIssueDto = new DetailedIssueDto();
    detailedIssueDto.setId(1);
    detailedIssueDto.setTitle("Test Issue");
    detailedIssueDto.setDescription("Test Description");
  }

  @Test
  void createIssue_createsAndReturnsIssue() {
    // Arrange
    when(userRepository.findById(1)).thenReturn(Optional.of(reportedByUser));
    when(userRepository.findById(2)).thenReturn(Optional.of(assignedToUser));
    when(projectRepository.findById(1)).thenReturn(Optional.of(project));
    when(issueRepository.save(any(Issue.class))).thenReturn(issue);
    when(issueMapper.toDto(any(Issue.class))).thenReturn(issueDto);

    // Act
    IssueDto result = issueService.createIssue(issueRequestDto);

    // Assert
    assertNotNull(result);
    assertEquals(1, result.getId());
    assertEquals("Test Issue", result.getTitle());

    verify(userRepository).findById(1);
    verify(userRepository).findById(2);
    verify(projectRepository).findById(1);
    verify(issueRepository).save(any(Issue.class));
    verify(notificationService).createNotification(anyString(), eq(NotificationType.CREATION));
    verify(issueMapper).toDto(any(Issue.class));
  }

  @Test
  void createIssue_throwsExceptionWhenReportedByUserNotFound() {
    // Arrange
    when(userRepository.findById(1)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(RuntimeException.class, () -> issueService.createIssue(issueRequestDto));
    verify(userRepository).findById(1);
    verify(issueRepository, never()).save(any(Issue.class));
    verify(notificationService, never())
        .createNotification(anyString(), any(NotificationType.class));
  }

  @Test
  void createIssue_throwsExceptionWhenAssignedToUserNotFound() {
    // Arrange
    when(userRepository.findById(1)).thenReturn(Optional.of(reportedByUser));
    when(userRepository.findById(2)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(RuntimeException.class, () -> issueService.createIssue(issueRequestDto));
    verify(userRepository).findById(1);
    verify(userRepository).findById(2);
    verify(issueRepository, never()).save(any(Issue.class));
    verify(notificationService, never())
        .createNotification(anyString(), any(NotificationType.class));
  }

  @Test
  void createIssue_throwsExceptionWhenProjectNotFound() {
    // Arrange
    when(userRepository.findById(1)).thenReturn(Optional.of(reportedByUser));
    when(userRepository.findById(2)).thenReturn(Optional.of(assignedToUser));
    when(projectRepository.findById(1)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(RuntimeException.class, () -> issueService.createIssue(issueRequestDto));
    verify(userRepository).findById(1);
    verify(userRepository).findById(2);
    verify(projectRepository).findById(1);
    verify(issueRepository, never()).save(any(Issue.class));
    verify(notificationService, never())
        .createNotification(anyString(), any(NotificationType.class));
  }

  @Test
  void getIssue_returnsIssue() {
    // Arrange
    when(issueRepository.findById(1)).thenReturn(Optional.of(issue));
    when(issueRepository.findTaskByIdWithComments(1)).thenReturn(Optional.of(issue));
    when(issueRepository.findTaskByIdWithAttachments(1)).thenReturn(Optional.of(issue));
    when(issueMapper.toDetailedIssueDto(any(Issue.class))).thenReturn(detailedIssueDto);

    // Act
    DetailedIssueDto result = issueService.getIssue(1);

    // Assert
    assertNotNull(result);
    assertEquals(1, result.getId());
    assertEquals("Test Issue", result.getTitle());
    assertEquals("Test Description", result.getDescription());

    verify(issueRepository).findById(1);
    verify(issueRepository).findTaskByIdWithComments(1);
    verify(issueRepository).findTaskByIdWithAttachments(1);
    verify(issueMapper).toDetailedIssueDto(any(Issue.class));
  }

  @Test
  void getIssue_throwsExceptionWhenIssueNotFound() {
    // Arrange
    when(issueRepository.findById(1)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(RuntimeException.class, () -> issueService.getIssue(1));
    verify(issueRepository).findById(1);
    verify(issueMapper, never()).toDetailedIssueDto(any(Issue.class));
  }

  @Test
  void getAllIssues_returnsAllIssues() {
    // Arrange
    List<Issue> issues =
        List.of(
            Issue.builder().id(1).title("Issue 1").build(),
            Issue.builder().id(2).title("Issue 2").build());
    when(issueRepository.findAll()).thenReturn(issues);
    when(issueMapper.toDto(any(Issue.class))).thenReturn(issueDto);

    // Act
    List<IssueDto> result = issueService.getAllIssues();

    // Assert
    assertNotNull(result);
    assertEquals(2, result.size());

    verify(issueRepository).findAll();
    verify(issueMapper, times(2)).toDto(any(Issue.class));
  }

  @Test
  void updateIssue_updatesAndReturnsIssue() {
    // Arrange
    when(issueRepository.findById(1)).thenReturn(Optional.of(issue));
    when(userRepository.findById(2)).thenReturn(Optional.of(assignedToUser));
    when(issueRepository.save(any(Issue.class))).thenReturn(issue);
    when(issueMapper.toDetailedIssueDto(any(Issue.class))).thenReturn(detailedIssueDto);

    // Act
    DetailedIssueDto result = issueService.updateIssue(1, issueRequestDto);

    // Assert
    assertNotNull(result);
    assertEquals(1, result.getId());
    assertEquals("Test Issue", result.getTitle());
    assertEquals("Test Description", result.getDescription());

    verify(issueRepository).findById(1);
    verify(userRepository).findById(2);
    verify(issueRepository).save(any(Issue.class));
    verify(notificationService).createNotification(anyString(), eq(NotificationType.UPDATE));
    verify(issueMapper).toDetailedIssueDto(any(Issue.class));
  }

  @Test
  void updateIssue_throwsExceptionWhenIssueNotFound() {
    // Arrange
    when(issueRepository.findById(1)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(RuntimeException.class, () -> issueService.updateIssue(1, issueRequestDto));
    verify(issueRepository).findById(1);
    verify(issueRepository, never()).save(any(Issue.class));
    verify(notificationService, never())
        .createNotification(anyString(), any(NotificationType.class));
  }

  @Test
  void updateIssue_throwsExceptionWhenAssignedUserNotFound() {
    // Arrange
    when(issueRepository.findById(1)).thenReturn(Optional.of(issue));
    when(userRepository.findById(2)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(RuntimeException.class, () -> issueService.updateIssue(1, issueRequestDto));
    verify(issueRepository).findById(1);
    verify(userRepository).findById(2);
    verify(issueRepository, never()).save(any(Issue.class));
    verify(notificationService, never())
        .createNotification(anyString(), any(NotificationType.class));
  }

  @Test
  void deleteIssue_deletesIssue() {
    // Arrange
    when(issueRepository.findById(1)).thenReturn(Optional.of(issue));

    // Act
    issueService.deleteIssue(1);

    // Assert
    verify(issueRepository).findById(1);
    verify(issueRepository).delete(issue);
    verify(notificationService).createNotification(anyString(), eq(NotificationType.DESTRUCTION));
  }

  @Test
  void deleteIssue_throwsExceptionWhenIssueNotFound() {
    // Arrange
    when(issueRepository.findById(1)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(RuntimeException.class, () -> issueService.deleteIssue(1));
    verify(issueRepository).findById(1);
    verify(issueRepository, never()).delete(any(Issue.class));
    verify(notificationService, never())
        .createNotification(anyString(), any(NotificationType.class));
  }
}
