package org.example.projectmanagementapi.service;

import org.example.projectmanagementapi.dto.CreateProjectDto;
import org.example.projectmanagementapi.dto.UpdateProjectDto;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ProjectServiceImpl projectService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createProject_createsAndReturnsProject() {
        CreateProjectDto createProjectDto = new CreateProjectDto("Project1", "Description1", new User(), "url1");
        Project project = new Project(1, "Project1", "Description1", ProjectStatus.ACTIVE, "url1", new User(), null);

        when(projectRepository.save(any(Project.class))).thenReturn(project);

        Project createdProject = projectService.createProject(createProjectDto);

        assertNotNull(createdProject);
        assertEquals("Project1", createdProject.getName());
        verify(projectRepository, times(1)).save(any(Project.class));
    }

    @Test
    void getProjectById_returnsProject() {
        Project project = new Project(1, "Project1", "Description1", ProjectStatus.ACTIVE, "url1", new User(), null);

        when(projectRepository.findById(1)).thenReturn(Optional.of(project));

        Project foundProject = projectService.getProjectById(1);

        assertNotNull(foundProject);
        assertEquals(1, foundProject.getId());
        verify(projectRepository, times(1)).findById(1);
    }

    @Test
    void getProjectById_throwsExceptionWhenNotFound() {
        when(projectRepository.findById(1)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> projectService.getProjectById(1));

        assertEquals("Project not found of id 1", exception.getMessage());
        verify(projectRepository, times(1)).findById(1);
    }

    @Test
    void getAllProjects_returnsAllProjects() {
        List<Project> projects = Arrays.asList(new Project(), new Project());

        when(projectRepository.findAll()).thenReturn(projects);

        List<Project> allProjects = projectService.getAllProjects();

        assertEquals(2, allProjects.size());
        verify(projectRepository, times(1)).findAll();
    }

    @Test
    void updateProject_updatesAndReturnsProject() {
        UpdateProjectDto updateProjectDto = new UpdateProjectDto("UpdatedName", "UpdatedDescription", ProjectStatus.COMPLETED, "updatedUrl");
        Project project = new Project(1, "Project1", "Description1", ProjectStatus.ACTIVE, "url1", new User(), null);

        when(projectRepository.findById(1)).thenReturn(Optional.of(project));
        when(projectRepository.save(any(Project.class))).thenReturn(project);

        Project updatedProject = projectService.updateProject(1, updateProjectDto);

        assertNotNull(updatedProject);
        assertEquals("UpdatedName", updatedProject.getName());
        verify(projectRepository, times(1)).findById(1);
        verify(projectRepository, times(1)).save(any(Project.class));
    }

    @Test
    void deleteProject_deletesProject() {
        Project project = new Project(1, "Project1", "Description1", ProjectStatus.ACTIVE, "url1", new User(), null);

        when(projectRepository.findById(1)).thenReturn(Optional.of(project));

        projectService.deleteProject(1);

        verify(projectRepository, times(1)).findById(1);
        verify(projectRepository, times(1)).delete(project);
    }

    @Test
    void getProjectMembers_returnsProjectMembers() {
        User user1 = new User();
        User user2 = new User();
        Project project = new Project(1, "Project1", "Description1", ProjectStatus.ACTIVE, "url1", new User(), Arrays.asList(user1, user2));

        when(projectRepository.findById(1)).thenReturn(Optional.of(project));

        List<User> members = projectService.getProjectMembers(1);

        assertEquals(2, members.size());
        verify(projectRepository, times(1)).findById(1);
    }

    @Test
    void addProjectMember_addsMemberToProject() {
        User user = new User();
        Project project = new Project(1, "Project1", "Description1", ProjectStatus.ACTIVE, "url1", new User(), Arrays.asList());

        when(projectRepository.findById(1)).thenReturn(Optional.of(project));
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(projectRepository.save(any(Project.class))).thenReturn(project);

        projectService.addProjectMember(1, 1);

        assertTrue(project.getUsers().contains(user));
        verify(projectRepository, times(1)).findById(1);
        verify(userRepository, times(1)).findById(1);
        verify(projectRepository, times(1)).save(any(Project.class));
    }

    @Test
    void removeProjectMember_removesMemberFromProject() {
        User user = new User();
        Project project = new Project(1, "Project1", "Description1", ProjectStatus.ACTIVE, "url1", new User(), Arrays.asList(user));

        when(projectRepository.findById(1)).thenReturn(Optional.of(project));
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(projectRepository.save(any(Project.class))).thenReturn(project);

        projectService.removeProjectMember(1, 1);

        assertFalse(project.getUsers().contains(user));
        verify(projectRepository, times(1)).findById(1);
        verify(userRepository, times(1)).findById(1);
        verify(projectRepository, times(1)).save(any(Project.class));
    }
}