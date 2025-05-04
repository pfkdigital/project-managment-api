package org.example.projectmanagementapi.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.example.projectmanagementapi.dto.response.DetailedProjectDto;
import org.example.projectmanagementapi.dto.request.ProjectRequestDto;
import org.example.projectmanagementapi.dto.response.ProjectWithCollaboratorsDto;
import org.example.projectmanagementapi.entity.Project;
import org.example.projectmanagementapi.entity.User;
import org.example.projectmanagementapi.enums.NotificationType;
import org.example.projectmanagementapi.enums.ProjectStatus;
import org.example.projectmanagementapi.mapper.ProjectMapper;
import org.example.projectmanagementapi.repository.ProjectRepository;
import org.example.projectmanagementapi.repository.UserRepository;
import org.example.projectmanagementapi.service.impl.ProjectServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

  @Mock private ProjectMapper projectMapper;

  @Mock private ProjectRepository projectRepository;

  @Mock private NotificationService notificationService;

  @Mock private UserRepository userRepository;

  @InjectMocks private ProjectServiceImpl projectService;

  private User owner;
  private Project project;
  private ProjectRequestDto projectRequestDto;
  private DetailedProjectDto detailedProjectDto;
  private ProjectWithCollaboratorsDto projectWithCollaboratorsDto;

  @BeforeEach
  void setUp() {
    owner = new User();
    owner.setId(1);
    owner.setUsername("owner");
    owner.setProjects(new ArrayList<>());
    owner.setOwnedProjects(new ArrayList<>());

    project =
        Project.builder()
            .id(1)
            .name("Test Project")
            .description("Test Description")
            .displayImageUrl("image.jpg")
            .status(ProjectStatus.ACTIVE)
            .collaborators(new ArrayList<>())
            .build();
    project.setOwner(owner);

    if (owner.getOwnedProjects() != null) {
      owner.getOwnedProjects().add(project);
    }

    projectRequestDto = new ProjectRequestDto();
    projectRequestDto.setName("Test Project");
    projectRequestDto.setDescription("Test Description");
    projectRequestDto.setDisplayImageUrl("image.jpg");
    projectRequestDto.setOwnerId(1);
    projectRequestDto.setStatus(ProjectStatus.ACTIVE);

    detailedProjectDto = new DetailedProjectDto();
    detailedProjectDto.setId(1);
    detailedProjectDto.setName("Test Project");

    projectWithCollaboratorsDto = new ProjectWithCollaboratorsDto();
    projectWithCollaboratorsDto.setId(1);
    projectWithCollaboratorsDto.setName("Test Project");
  }

  @Test
  void createProject_createsAndReturnsProject() {
    when(userRepository.findById(1)).thenReturn(Optional.of(owner));
    when(projectRepository.save(any(Project.class))).thenReturn(project);
    when(projectMapper.toProjectWithCollaborators(any(Project.class)))
        .thenReturn(projectWithCollaboratorsDto);

    ProjectWithCollaboratorsDto result = projectService.createProject(projectRequestDto);

    assertNotNull(result);
    assertEquals(projectWithCollaboratorsDto.getId(), result.getId());
    assertEquals(projectWithCollaboratorsDto.getName(), result.getName());

    verify(userRepository).findById(1);
    verify(projectRepository).save(any(Project.class));
    verify(notificationService).createNotification(anyString(), eq(NotificationType.CREATION));
  }

  @Test
  void createProject_throwsExceptionWhenOwnerNotFound() {
    when(userRepository.findById(1)).thenReturn(Optional.empty());

    assertThrows(RuntimeException.class, () -> projectService.createProject(projectRequestDto));

    verify(userRepository).findById(1);
    verify(projectRepository, never()).save(any(Project.class));
    verify(notificationService, never())
        .createNotification(anyString(), any(NotificationType.class));
  }

  @Test
  void getProjectById_returnsProject() {
    when(projectRepository.findById(1)).thenReturn(Optional.of(project));
    when(projectRepository.findProjectWithOwnerById(1)).thenReturn(Optional.of(project));
    when(projectRepository.findProjectWithCollaboratorsById(1)).thenReturn(Optional.of(project));
    when(projectRepository.findProjectWithTasksById(1)).thenReturn(Optional.of(project));
    when(projectRepository.findProjectWithIssuesById(1)).thenReturn(Optional.of(project));
    when(projectMapper.toDetailedProjectDto(any(Project.class))).thenReturn(detailedProjectDto);

    DetailedProjectDto result = projectService.getProjectById(1);

    assertNotNull(result);
    assertEquals(detailedProjectDto.getId(), result.getId());
    assertEquals(detailedProjectDto.getName(), result.getName());

    verify(projectRepository).findById(1);
  }

  @Test
  void getProjectById_throwsExceptionWhenProjectNotFound() {
    when(projectRepository.findById(1)).thenReturn(Optional.empty());

    assertThrows(RuntimeException.class, () -> projectService.getProjectById(1));

    verify(projectRepository).findById(1);
  }

  @Test
  void getAllProjects_returnsAllProjects() {
    List<Project> projects = List.of(project);
    when(projectRepository.findAll()).thenReturn(projects);
    when(projectMapper.toProjectWithCollaborators(any(Project.class)))
        .thenReturn(projectWithCollaboratorsDto);

    List<ProjectWithCollaboratorsDto> result = projectService.getAllProjects();

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(projectWithCollaboratorsDto.getId(), result.get(0).getId());
    assertEquals(projectWithCollaboratorsDto.getName(), result.get(0).getName());

    verify(projectRepository).findAll();
  }

  @Test
  void updateProject_updatesAndReturnsProject() {
    User newOwner = new User();
    newOwner.setId(2);
    newOwner.setUsername("newOwner");
    newOwner.setProjects(new ArrayList<>());
    newOwner.setOwnedProjects(new ArrayList<>()); // Add this line to initialize ownedProjects

    projectRequestDto.setOwnerId(2);

    when(projectRepository.findById(1)).thenReturn(Optional.of(project));
    when(userRepository.findById(2)).thenReturn(Optional.of(newOwner));
    when(projectRepository.save(any(Project.class))).thenReturn(project);
    when(projectMapper.toDetailedProjectDto(any(Project.class))).thenReturn(detailedProjectDto);

    DetailedProjectDto result = projectService.updateProject(1, projectRequestDto);

    assertNotNull(result);
    assertEquals(detailedProjectDto.getId(), result.getId());

    verify(projectRepository).findById(1);
    verify(userRepository).findById(2);
    verify(projectRepository).save(any(Project.class));
    verify(notificationService).createNotification(anyString(), eq(NotificationType.UPDATE));
  }

  @Test
  void updateProject_sameOwner_updatesAndReturnsProject() {
    when(projectRepository.findById(1)).thenReturn(Optional.of(project));
    when(userRepository.findById(1)).thenReturn(Optional.of(owner));
    when(projectRepository.save(any(Project.class))).thenReturn(project);
    when(projectMapper.toDetailedProjectDto(any(Project.class))).thenReturn(detailedProjectDto);

    DetailedProjectDto result = projectService.updateProject(1, projectRequestDto);

    assertNotNull(result);
    assertEquals(detailedProjectDto.getId(), result.getId());

    verify(projectRepository).findById(1);
    verify(userRepository).findById(1);
    verify(projectRepository).save(any(Project.class));
    verify(notificationService).createNotification(anyString(), eq(NotificationType.UPDATE));
  }

  @Test
  void updateProject_throwsExceptionWhenProjectNotFound() {
    when(projectRepository.findById(1)).thenReturn(Optional.empty());

    assertThrows(RuntimeException.class, () -> projectService.updateProject(1, projectRequestDto));

    verify(projectRepository).findById(1);
    verify(projectRepository, never()).save(any(Project.class));
    verify(notificationService, never())
        .createNotification(anyString(), any(NotificationType.class));
  }

  @Test
  void updateProject_throwsExceptionWhenUserNotFound() {
    when(projectRepository.findById(1)).thenReturn(Optional.of(project));
    when(userRepository.findById(1)).thenReturn(Optional.empty());

    assertThrows(RuntimeException.class, () -> projectService.updateProject(1, projectRequestDto));

    verify(projectRepository).findById(1);
    verify(userRepository).findById(1);
    verify(projectRepository, never()).save(any(Project.class));
    verify(notificationService, never())
        .createNotification(anyString(), any(NotificationType.class));
  }

  @Test
  void deleteProject_deletesProject() {
    when(projectRepository.findById(1)).thenReturn(Optional.of(project));

    assertDoesNotThrow(() -> projectService.deleteProject(1));

    verify(projectRepository).findById(1);
    verify(projectRepository).delete(project);
    verify(notificationService).createNotification(anyString(), eq(NotificationType.DESTRUCTION));
  }

  @Test
  void deleteProject_throwsExceptionWhenProjectNotFound() {
    when(projectRepository.findById(1)).thenReturn(Optional.empty());

    assertThrows(RuntimeException.class, () -> projectService.deleteProject(1));

    verify(projectRepository).findById(1);
    verify(projectRepository, never()).delete(any(Project.class));
    verify(notificationService, never())
        .createNotification(anyString(), any(NotificationType.class));
  }

  @Test
  void addProjectMember_addsUserToProject() {
    User member = new User();
    member.setId(2);
    member.setUsername("member");
    member.setProjects(new ArrayList<>()); // Initialize projects list

    when(projectRepository.findById(1)).thenReturn(Optional.of(project));
    when(projectRepository.findProjectWithOwnerById(1)).thenReturn(Optional.of(project));
    when(projectRepository.findProjectWithCollaboratorsById(1)).thenReturn(Optional.of(project));
    when(projectRepository.findProjectWithTasksById(1)).thenReturn(Optional.of(project));
    when(projectRepository.findProjectWithIssuesById(1)).thenReturn(Optional.of(project));
    when(userRepository.findById(2)).thenReturn(Optional.of(member));

    assertDoesNotThrow(() -> projectService.addProjectMember(1, 2));

    verify(projectRepository).findById(1);
    verify(userRepository).findById(2);
    verify(projectRepository).save(project);
    verify(notificationService).createNotification(anyString(), eq(NotificationType.CREATION));
  }

  @Test
  void addProjectMember_throwsExceptionWhenProjectNotFound() {
    when(projectRepository.findById(1)).thenReturn(Optional.empty());

    assertThrows(RuntimeException.class, () -> projectService.addProjectMember(1, 2));

    verify(projectRepository).findById(1);
    verify(userRepository, never()).findById(anyInt());
    verify(projectRepository, never()).save(any(Project.class));
    verify(notificationService, never())
        .createNotification(anyString(), any(NotificationType.class));
  }

  @Test
  void addProjectMember_throwsExceptionWhenUserNotFound() {
    when(projectRepository.findById(1)).thenReturn(Optional.of(project));
    when(projectRepository.findProjectWithOwnerById(1)).thenReturn(Optional.of(project));
    when(projectRepository.findProjectWithCollaboratorsById(1)).thenReturn(Optional.of(project));
    when(projectRepository.findProjectWithTasksById(1)).thenReturn(Optional.of(project));
    when(projectRepository.findProjectWithIssuesById(1)).thenReturn(Optional.of(project));
    when(userRepository.findById(2)).thenReturn(Optional.empty());

    assertThrows(RuntimeException.class, () -> projectService.addProjectMember(1, 2));

    verify(projectRepository).findById(1);
    verify(userRepository).findById(2);
    verify(projectRepository, never()).save(any(Project.class));
    verify(notificationService, never())
        .createNotification(anyString(), any(NotificationType.class));
  }

  @Test
  void removeProjectMember_removesUserFromProject() {
    User member = new User();
    member.setId(2);
    member.setUsername("member");
    member.setProjects(new ArrayList<>()); // Initialize projects list

    // Need this additional collection initialization
    owner.setOwnedProjects(new ArrayList<>());

    project.getCollaborators().add(member);

    when(projectRepository.findById(1)).thenReturn(Optional.of(project));
    when(projectRepository.findProjectWithOwnerById(1)).thenReturn(Optional.of(project));
    when(projectRepository.findProjectWithCollaboratorsById(1)).thenReturn(Optional.of(project));
    when(projectRepository.findProjectWithTasksById(1)).thenReturn(Optional.of(project));
    when(projectRepository.findProjectWithIssuesById(1)).thenReturn(Optional.of(project));
    when(userRepository.findById(2)).thenReturn(Optional.of(member));

    assertDoesNotThrow(() -> projectService.removeProjectMember(1, 2));

    verify(projectRepository).findById(1);
    verify(userRepository).findById(2);
    verify(projectRepository).save(project);
    verify(notificationService).createNotification(anyString(), eq(NotificationType.DESTRUCTION));
  }

  @Test
  void removeProjectMember_throwsExceptionWhenProjectNotFound() {
    when(projectRepository.findById(1)).thenReturn(Optional.empty());

    assertThrows(RuntimeException.class, () -> projectService.removeProjectMember(1, 2));

    verify(projectRepository).findById(1);
    verify(userRepository, never()).findById(anyInt());
    verify(projectRepository, never()).save(any(Project.class));
    verify(notificationService, never())
        .createNotification(anyString(), any(NotificationType.class));
  }

  @Test
  void removeProjectMember_throwsExceptionWhenUserNotFound() {
    when(projectRepository.findById(1)).thenReturn(Optional.of(project));
    when(projectRepository.findProjectWithOwnerById(1)).thenReturn(Optional.of(project));
    when(projectRepository.findProjectWithCollaboratorsById(1)).thenReturn(Optional.of(project));
    when(projectRepository.findProjectWithTasksById(1)).thenReturn(Optional.of(project));
    when(projectRepository.findProjectWithIssuesById(1)).thenReturn(Optional.of(project));
    when(userRepository.findById(2)).thenReturn(Optional.empty());

    assertThrows(RuntimeException.class, () -> projectService.removeProjectMember(1, 2));

    verify(projectRepository).findById(1);
    verify(userRepository).findById(2);
    verify(projectRepository, never()).save(any(Project.class));
    verify(notificationService, never())
        .createNotification(anyString(), any(NotificationType.class));
  }
}
