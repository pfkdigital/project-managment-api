package org.example.projectmanagementapi.service.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;
import org.example.projectmanagementapi.dto.response.DetailedProjectDto;
import org.example.projectmanagementapi.dto.request.ProjectRequestDto;
import org.example.projectmanagementapi.dto.response.ProjectWithUsersDto;
import org.example.projectmanagementapi.entity.Project;
import org.example.projectmanagementapi.entity.User;
import org.example.projectmanagementapi.enums.NotificationType;
import org.example.projectmanagementapi.enums.ProjectStatus;
import org.example.projectmanagementapi.mapper.ProjectMapper;
import org.example.projectmanagementapi.repository.ProjectRepository;
import org.example.projectmanagementapi.repository.UserRepository;
import org.example.projectmanagementapi.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {

    @Mock private ProjectMapper projectMapper;
    @Mock private ProjectRepository projectRepository;
    @Mock private NotificationService notificationService;
    @Mock private UserRepository userRepository;

    @InjectMocks private ProjectServiceImpl projectService;

    private Project project;
    private User user;
    private ProjectRequestDto projectRequestDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1);
        user.setUsername("testuser");

        project = new Project();
        project.setId(1);
        project.setName("Test Project");
        project.setDescription("Test Description");
        project.setStatus(ProjectStatus.ACTIVE);
        project.setOwner(user);

        projectRequestDto = new ProjectRequestDto();
        projectRequestDto.setName("Test Project");
        projectRequestDto.setDescription("Test Description");
        projectRequestDto.setOwnerId(1);
    }

    @Test
    void testCreateProject() {
        when(userRepository.findById(any(Integer.class))).thenReturn(Optional.of(user));
        when(projectRepository.save(any(Project.class))).thenReturn(project);
        when(projectMapper.toProjectWithUsersDto(any(Project.class)))
                .thenReturn(new ProjectWithUsersDto());

        projectService.createProject(projectRequestDto);

        verify(projectRepository, times(1)).save(any(Project.class));
        verify(notificationService, times(1))
                .createNotification(anyString(), eq(NotificationType.CREATION));
    }

    @Test
    void testGetProjectById() {
        when(projectRepository.findById(any(Integer.class))).thenReturn(Optional.of(project));
        when(projectMapper.toDetailedProjectDto(any(Project.class)))
                .thenReturn(new DetailedProjectDto());

        projectService.getProjectById(1);

        verify(projectRepository, times(1)).findById(1);
    }

    @Test
    void testUpdateProject() {
        when(userRepository.findById(any(Integer.class))).thenReturn(Optional.of(user));
        when(projectRepository.findById(any(Integer.class))).thenReturn(Optional.of(project));
        when(projectRepository.save(any(Project.class))).thenReturn(project);
        when(projectMapper.toDetailedProjectDto(any(Project.class)))
                .thenReturn(new DetailedProjectDto());

        projectService.updateProject(1, projectRequestDto);

        verify(projectRepository, times(1)).save(any(Project.class));
        verify(notificationService, times(1))
                .createNotification(anyString(), eq(NotificationType.UPDATE));
    }

    @Test
    void testDeleteProject() {
        when(projectRepository.findById(any(Integer.class))).thenReturn(Optional.of(project));

        projectService.deleteProject(1);

        verify(projectRepository, times(1)).delete(any(Project.class));
        verify(notificationService, times(1))
                .createNotification(anyString(), eq(NotificationType.DESTRUCTION));
    }

    @Test
    void testAddProjectMember() {
        when(projectRepository.findById(any(Integer.class))).thenReturn(Optional.of(project));
        when(userRepository.findById(any(Integer.class))).thenReturn(Optional.of(user));

        projectService.addProjectMember(1, 1);

        verify(projectRepository, times(1)).save(any(Project.class));
        verify(notificationService, times(1))
                .createNotification(anyString(), eq(NotificationType.CREATION));
    }

    @Test
    void testRemoveProjectMember() {
        when(projectRepository.findById(any(Integer.class))).thenReturn(Optional.of(project));
        when(userRepository.findById(any(Integer.class))).thenReturn(Optional.of(user));

        projectService.removeProjectMember(1, 1);

        verify(projectRepository, times(1)).save(any(Project.class));
        verify(notificationService, times(1))
                .createNotification(anyString(), eq(NotificationType.DESTRUCTION));
    }
}