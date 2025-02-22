package org.example.projectmanagementapi.controller;

import lombok.RequiredArgsConstructor;
import org.example.projectmanagementapi.dto.ProjectDto;
import org.example.projectmanagementapi.service.impl.ProjectServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
public class ProjectController {
  private final ProjectServiceImpl projectService;

  @PostMapping
  public ResponseEntity<?> createProject(@RequestBody ProjectDto projectDto) {
    return new ResponseEntity<>(projectService.createProject(projectDto), HttpStatus.CREATED);
  }

  @GetMapping("/{projectId}")
  public ResponseEntity<?> getProjectById(@PathVariable Integer projectId) {
    return new ResponseEntity<>(projectService.getProjectById(projectId), HttpStatus.OK);
  }

  @GetMapping
  public ResponseEntity<?> getAllProjects() {
    return new ResponseEntity<>(projectService.getAllProjects(), HttpStatus.OK);
  }

  @PutMapping("/{projectId}")
  public ResponseEntity<?> updateProject(
      @PathVariable Integer projectId, @RequestBody ProjectDto projectDto) {
    return new ResponseEntity<>(projectService.updateProject(projectId, projectDto), HttpStatus.OK);
  }

  @PutMapping("/{projectId}/members/{userId}")
  public ResponseEntity<?> addProjectMember(
      @PathVariable Integer projectId, @PathVariable Integer userId) {
    projectService.addProjectMember(projectId, userId);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @DeleteMapping("/{projectId}/members/{userId}")
  public ResponseEntity<?> removeProjectMember(
      @PathVariable Integer projectId, @PathVariable Integer userId) {
    projectService.removeProjectMember(projectId, userId);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @DeleteMapping("/{projectId}")
  public ResponseEntity<?> deleteProject(@PathVariable Integer projectId) {
    projectService.deleteProject(projectId);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
