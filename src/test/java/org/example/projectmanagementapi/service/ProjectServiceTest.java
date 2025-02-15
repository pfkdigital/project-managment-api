package org.example.projectmanagementapi.service;

import org.example.projectmanagementapi.dto.ProjectDto;
import org.example.projectmanagementapi.entity.Notification;
import org.example.projectmanagementapi.entity.Project;
import org.example.projectmanagementapi.entity.User;
import org.example.projectmanagementapi.enums.ProjectStatus;
import org.example.projectmanagementapi.repository.ProjectRepository;
import org.example.projectmanagementapi.repository.UserRepository;
import org.example.projectmanagementapi.service.impl.ProjectServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ProjectServiceImpl projectService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createProject_createsProjectWithOwner() {
        ProjectDto projectDto = new ProjectDto("Project1", "Description1", ProjectStatus.INACTIVE, 1, "url1");
        User owner = new User();
        when(userRepository.findById(1)).thenReturn(Optional.of(owner));
        when(projectRepository.save(any(Project.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Project createdProject = projectService.createProject(projectDto);

        assertNotNull(createdProject);
        assertEquals("Project1", createdProject.getName());
        assertEquals(owner, createdProject.getOwner());
        verify(projectRepository, times(1)).save(any(Project.class));
        verify(notificationService, times(1)).createNotification(any(Notification.class));
    }

    @Test
    void createProject_throwsExceptionWhenOwnerNotFound() {
        ProjectDto projectDto = new ProjectDto("Project1", "Description1", ProjectStatus.INACTIVE, 1, "url1");
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> projectService.createProject(projectDto));
        verify(projectRepository, never()).save(any(Project.class));
        verify(notificationService, never()).createNotification(any(Notification.class));
    }

    @Test
    void updateProject_updatesProjectWithOwner() {
        ProjectDto projectDto = new ProjectDto("UpdatedName", "UpdatedDescription", ProjectStatus.INACTIVE, 1, "updatedUrl");
        User owner = new User();
        Project project = Project.builder()
                .id(1)
                .name("Project1")
                .description("Description1")
                .status(ProjectStatus.ACTIVE)
                .displayImageUrl("url1")
                .owner(new User())
                .users(new ArrayList<>()) // Initialize users list
                .build();

        when(projectRepository.findById(1)).thenReturn(Optional.of(project));
        when(userRepository.findById(1)).thenReturn(Optional.of(owner));
        when(projectRepository.save(any(Project.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Project updatedProject = projectService.updateProject(1, projectDto);

        assertNotNull(updatedProject);
        assertEquals("UpdatedName", updatedProject.getName());
        assertEquals(owner, updatedProject.getOwner());
        verify(projectRepository, times(1)).findById(1);
        verify(projectRepository, times(1)).save(any(Project.class));
        verify(notificationService, times(1)).createNotification(any(Notification.class));
    }

    @Test
    void updateProject_throwsExceptionWhenProjectNotFound() {
        ProjectDto projectDto = new ProjectDto("UpdatedName", "UpdatedDescription", ProjectStatus.INACTIVE, 1, "updatedUrl");
        when(projectRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> projectService.updateProject(1, projectDto));
        verify(projectRepository, never()).save(any(Project.class));
        verify(notificationService, never()).createNotification(any(Notification.class));
    }

    @Test
    void updateProject_throwsExceptionWhenOwnerNotFound() {
        ProjectDto projectDto = new ProjectDto("UpdatedName", "UpdatedDescription", ProjectStatus.INACTIVE, 1, "updatedUrl");
        Project project = Project.builder()
                .id(1)
                .name("Project1")
                .description("Description1")
                .status(ProjectStatus.ACTIVE)
                .displayImageUrl("url1")
                .owner(new User())
                .users(new ArrayList<>()) // Initialize users list
                .build();

        when(projectRepository.findById(1)).thenReturn(Optional.of(project));
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> projectService.updateProject(1, projectDto));
        verify(projectRepository, never()).save(any(Project.class));
        verify(notificationService, never()).createNotification(any(Notification.class));
    }

    @Test
    void addProjectMember_throwsExceptionWhenProjectNotFound() {
        when(projectRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> projectService.addProjectMember(1, 1));
        verify(userRepository, never()).findById(1);
        verify(projectRepository, never()).save(any(Project.class));
        verify(notificationService, never()).createNotification(any(Notification.class));
    }

    @Test
    void addProjectMember_throwsExceptionWhenUserNotFound() {
        Project project = Project.builder()
                .id(1)
                .name("Project1")
                .description("Description1")
                .status(ProjectStatus.ACTIVE)
                .displayImageUrl("url1")
                .owner(new User())
                .users(new ArrayList<>()) // Initialize users list
                .build();

        when(projectRepository.findById(1)).thenReturn(Optional.of(project));
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> projectService.addProjectMember(1, 1));
        verify(projectRepository, never()).save(any(Project.class));
        verify(notificationService, never()).createNotification(any(Notification.class));
    }

    @Test
    void removeProjectMember_throwsExceptionWhenProjectNotFound() {
        when(projectRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> projectService.removeProjectMember(1, 1));
        verify(userRepository, never()).findById(1);
        verify(projectRepository, never()).save(any(Project.class));
        verify(notificationService, never()).createNotification(any(Notification.class));
    }

    @Test
    void removeProjectMember_throwsExceptionWhenUserNotFound() {
        Project project = Project.builder()
                .id(1)
                .name("Project1")
                .description("Description1")
                .status(ProjectStatus.ACTIVE)
                .displayImageUrl("url1")
                .owner(new User())
                .users(new ArrayList<>()) // Initialize users list
                .build();

        when(projectRepository.findById(1)).thenReturn(Optional.of(project));
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> projectService.removeProjectMember(1, 1));
        verify(projectRepository, never()).save(any(Project.class));
        verify(notificationService, never()).createNotification(any(Notification.class));
    }
}