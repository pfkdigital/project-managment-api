package org.example.projectmanagementapi.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;
import org.example.projectmanagementapi.dto.request.ProjectRequestDto;
import org.example.projectmanagementapi.dto.response.DetailedProjectDto;
import org.example.projectmanagementapi.dto.response.ProjectWithUsersDto;
import org.example.projectmanagementapi.service.impl.ProjectServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ProjectControllerTest {

    @Mock private ProjectServiceImpl projectService;

    @InjectMocks private ProjectController projectController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateProject() {
    ProjectWithUsersDto projectDto = new ProjectWithUsersDto();
        when(projectService.createProject(any(ProjectRequestDto.class))).thenReturn(projectDto);

        ResponseEntity<?> response = projectController.createProject(new ProjectRequestDto());

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(projectDto, response.getBody());
    }

    @Test
    void testGetProjects() {
        List<ProjectWithUsersDto> projects = Collections.emptyList();
        when(projectService.getAllProjects()).thenReturn(projects);

        ResponseEntity<?> response = projectController.getAllProjects();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(projects, response.getBody());
    }

    @Test
    void testGetProject() {
        DetailedProjectDto projectDto = new DetailedProjectDto();
        when(projectService.getProjectById(anyInt())).thenReturn(projectDto);

        ResponseEntity<?> response = projectController.getProjectById(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(projectDto, response.getBody());
    }

    @Test
    void testUpdateProject() {
        DetailedProjectDto projectDto = new DetailedProjectDto();
        when(projectService.updateProject(anyInt(), any(ProjectRequestDto.class))).thenReturn(projectDto);

        ResponseEntity<?> response = projectController.updateProject(1,new ProjectRequestDto());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(projectDto, response.getBody());
    }

    @Test
    void testDeleteProject() {
        doNothing().when(projectService).deleteProject(anyInt());

        ResponseEntity<?> response = projectController.deleteProject(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(projectService, times(1)).deleteProject(1);
    }
}