//package org.example.projectmanagementapi.controller;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyInt;
//import static org.mockito.Mockito.*;
//
//import org.example.projectmanagementapi.dto.ProjectDto;
//import org.example.projectmanagementapi.entity.Project;
//import org.example.projectmanagementapi.enums.ProjectStatus;
//import org.example.projectmanagementapi.service.impl.ProjectServiceImpl;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//
//class ProjectControllerTest {
//
//  @Mock private ProjectServiceImpl projectService;
//
//  @InjectMocks private ProjectController projectController;
//
//  @BeforeEach
//  void setUp() {
//    MockitoAnnotations.openMocks(this);
//  }
//
//  @Test
//  void testCreateProject() {
//    Project project =
//        Project.builder()
//            .name("Project1")
//            .description("Description1")
//            .status(ProjectStatus.INACTIVE)
//            .build();
//    when(projectService.createProject(any(ProjectDto.class))).thenReturn(project);
//
//    ResponseEntity<?> response =
//        projectController.createProject(
//            new ProjectDto("Project1", "Description1", ProjectStatus.INACTIVE, 1, "url1"));
//
//    assertEquals(HttpStatus.CREATED, response.getStatusCode());
//    assertEquals(project, response.getBody());
//  }
//
//  @Test
//  void testGetProjectById() {
//    Project project =
//        Project.builder()
//            .name("Project1")
//            .description("Description1")
//            .status(ProjectStatus.INACTIVE)
//            .build();
//    when(projectService.getProjectById(anyInt())).thenReturn(project);
//
//    ResponseEntity<?> response = projectController.getProjectById(1);
//
//    assertEquals(HttpStatus.OK, response.getStatusCode());
//    assertEquals(project, response.getBody());
//  }
//
//  @Test
//  void testUpdateProject() {
//    Project project =
//        Project.builder()
//            .name("Project1")
//            .description("Description1")
//            .status(ProjectStatus.INACTIVE)
//            .build();
//    when(projectService.updateProject(anyInt(), any(ProjectDto.class))).thenReturn(project);
//
//    ResponseEntity<?> response =
//        projectController.updateProject(
//            1, new ProjectDto("Project1", "Description1", ProjectStatus.INACTIVE, 1, "url1"));
//
//    assertEquals(HttpStatus.OK, response.getStatusCode());
//    assertEquals(project, response.getBody());
//  }
//}
