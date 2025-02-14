package org.example.projectmanagementapi.service;

import org.example.projectmanagementapi.dto.CreateProjectDto;
import org.example.projectmanagementapi.dto.UpdateProjectDto;
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

import java.util.Arrays;
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
    void createProject_createsAndReturnsProjectWithNotification() {
        CreateProjectDto createProjectDto = new CreateProjectDto("Project1", "Description1", new User(), "url1");
        Project project = Project.builder()
                .id(1)
                .name("Project1")
                .description("Description1")
                .status(ProjectStatus.ACTIVE)
                .displayImageUrl("url1")
                .owner(new User())
                .build();

        when(projectRepository.save(any(Project.class))).thenReturn(project);
        doNothing().when(notificationService).createNotification(any(Notification.class));

        Project createdProject = projectService.createProject(createProjectDto);

        assertNotNull(createdProject);
        assertEquals("Project1", createdProject.getName());
        verify(projectRepository, times(1)).save(any(Project.class));
        verify(notificationService, times(1)).createNotification(any(Notification.class));
    }

    @Test
    void updateProject_updatesAndReturnsProjectWithNotification() {
        UpdateProjectDto updateProjectDto = new UpdateProjectDto("UpdatedName", "UpdatedDescription", ProjectStatus.COMPLETED, "updatedUrl");
        Project project = Project.builder()
                .id(1)
                .name("Project1")
                .description("Description1")
                .status(ProjectStatus.ACTIVE)
                .displayImageUrl("url1")
                .owner(new User())
                .build();

        when(projectRepository.findById(1)).thenReturn(Optional.of(project));
        when(projectRepository.save(any(Project.class))).thenReturn(project);
        doNothing().when(notificationService).createNotification(any(Notification.class));

        Project updatedProject = projectService.updateProject(1, updateProjectDto);

        assertNotNull(updatedProject);
        assertEquals("UpdatedName", updatedProject.getName());
        verify(projectRepository, times(1)).findById(1);
        verify(projectRepository, times(1)).save(any(Project.class));
        verify(notificationService, times(1)).createNotification(any(Notification.class));
    }

    @Test
    void deleteProject_deletesProjectWithNotification() {
        Project project = Project.builder()
                .id(1)
                .name("Project1")
                .description("Description1")
                .status(ProjectStatus.ACTIVE)
                .displayImageUrl("url1")
                .owner(new User())
                .build();

        when(projectRepository.findById(1)).thenReturn(Optional.of(project));
        doNothing().when(notificationService).createNotification(any(Notification.class));
        doNothing().when(projectRepository).delete(any(Project.class));

        projectService.deleteProject(1);

        verify(projectRepository, times(1)).findById(1);
        verify(projectRepository, times(1)).delete(any(Project.class));
        verify(notificationService, times(1)).createNotification(any(Notification.class));
    }

    @Test
    void addProjectMember_addsMemberToProjectWithNotification() {
        User user = new User();
        Project project = Project.builder()
                .id(1)
                .name("Project1")
                .description("Description1")
                .status(ProjectStatus.ACTIVE)
                .displayImageUrl("url1")
                .owner(new User())
                .users(Arrays.asList())
                .build();

        when(projectRepository.findById(1)).thenReturn(Optional.of(project));
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(projectRepository.save(any(Project.class))).thenReturn(project);
        doNothing().when(notificationService).createNotification(any(Notification.class));

        projectService.addProjectMember(1, 1);

        assertTrue(project.getUsers().contains(user));
        verify(projectRepository, times(1)).findById(1);
        verify(userRepository, times(1)).findById(1);
        verify(projectRepository, times(1)).save(any(Project.class));
        verify(notificationService, times(1)).createNotification(any(Notification.class));
    }

    @Test
    void removeProjectMember_removesMemberFromProjectWithNotification() {
        User user = new User();
        Project project = Project.builder()
                .id(1)
                .name("Project1")
                .description("Description1")
                .status(ProjectStatus.ACTIVE)
                .displayImageUrl("url1")
                .owner(new User())
                .users(Arrays.asList(user))
                .build();

        when(projectRepository.findById(1)).thenReturn(Optional.of(project));
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(projectRepository.save(any(Project.class))).thenReturn(project);
        doNothing().when(notificationService).createNotification(any(Notification.class));

        projectService.removeProjectMember(1, 1);

        assertFalse(project.getUsers().contains(user));
        verify(projectRepository, times(1)).findById(1);
        verify(userRepository, times(1)).findById(1);
        verify(projectRepository, times(1)).save(any(Project.class));
        verify(notificationService, times(1)).createNotification(any(Notification.class));
    }
}