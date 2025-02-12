package org.example.projectmanagementapi.controller;

import org.example.projectmanagementapi.dto.CreateProjectDto;
import org.example.projectmanagementapi.dto.UpdateProjectDto;
import org.example.projectmanagementapi.entity.Project;
import org.example.projectmanagementapi.entity.User;
import org.example.projectmanagementapi.enums.ProjectStatus;
import org.example.projectmanagementapi.service.impl.ProjectServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ProjectControllerTest {

    @Mock
    private ProjectServiceImpl projectService;

    @InjectMocks
    private ProjectController projectController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createProject_returnsCreatedProject() {
        CreateProjectDto createProjectDto = new CreateProjectDto("Project1", "Description1", new User(), "url1");
        Project project = new Project(1, "Project1", "Description1", ProjectStatus.ACTIVE, "url1", new User(), null);

        when(projectService.createProject(createProjectDto)).thenReturn(project);

        ResponseEntity<?> response = projectController.createProject(createProjectDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(project, response.getBody());
        verify(projectService, times(1)).createProject(createProjectDto);
    }

    @Test
    void getProjectById_returnsProject() {
        Project project = new Project(1, "Project1", "Description1", ProjectStatus.ACTIVE, "url1", new User(), null);

        when(projectService.getProjectById(1)).thenReturn(project);

        ResponseEntity<?> response = projectController.getProjectById(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(project, response.getBody());
        verify(projectService, times(1)).getProjectById(1);
    }

    @Test
    void getAllProjects_returnsAllProjects() {
        List<Project> projects = Arrays.asList(new Project(), new Project());

        when(projectService.getAllProjects()).thenReturn(projects);

        ResponseEntity<?> response = projectController.getAllProjects();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(projects, response.getBody());
        verify(projectService, times(1)).getAllProjects();
    }

    @Test
    void updateProject_returnsUpdatedProject() {
        UpdateProjectDto updateProjectDto = new UpdateProjectDto("UpdatedName", "UpdatedDescription", ProjectStatus.COMPLETED, "updatedUrl");
        Project project = new Project(1, "Project1", "Description1", ProjectStatus.ACTIVE, "url1", new User(), null);

        when(projectService.updateProject(1, updateProjectDto)).thenReturn(project);

        ResponseEntity<?> response = projectController.updateProject(1, updateProjectDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(project, response.getBody());
        verify(projectService, times(1)).updateProject(1, updateProjectDto);
    }

    @Test
    void deleteProject_returnsOkStatus() {
        doNothing().when(projectService).deleteProject(1);

        ResponseEntity<?> response = projectController.deleteProject(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(projectService, times(1)).deleteProject(1);
    }

    @Test
    void addProjectMember_returnsOkStatus() {
        doNothing().when(projectService).addProjectMember(1, 1);

        ResponseEntity<?> response = projectController.addProjectMember(1, 1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(projectService, times(1)).addProjectMember(1, 1);
    }

    @Test
    void removeProjectMember_returnsOkStatus() {
        doNothing().when(projectService).removeProjectMember(1, 1);

        ResponseEntity<?> response = projectController.removeProjectMember(1, 1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(projectService, times(1)).removeProjectMember(1, 1);
    }
}