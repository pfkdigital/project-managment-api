package org.example.projectmanagementapi.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.projectmanagementapi.dto.request.ProjectRequestDto;
import org.example.projectmanagementapi.dto.response.DetailedProjectDto;
import org.example.projectmanagementapi.dto.response.ProjectWithCollaboratorsDto;
import org.example.projectmanagementapi.service.impl.ProjectServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
public class ProjectController {
  private final ProjectServiceImpl projectService;

  @PostMapping
  public ResponseEntity<ProjectWithCollaboratorsDto> createProject(
      @Valid @RequestBody ProjectRequestDto projectRequestDto) {
    return new ResponseEntity<>(
        projectService.createProject(projectRequestDto), HttpStatus.CREATED);
  }

  @GetMapping("/{projectId}")
  public ResponseEntity<DetailedProjectDto> getProjectById(@PathVariable Integer projectId) {
    return new ResponseEntity<>(projectService.getProjectById(projectId), HttpStatus.OK);
  }

  @GetMapping
  public ResponseEntity<List<ProjectWithCollaboratorsDto>> getAllProjects() {
    return new ResponseEntity<>(projectService.getAllProjects(), HttpStatus.OK);
  }

  @PutMapping("/{projectId}")
  public ResponseEntity<DetailedProjectDto> updateProject(
      @PathVariable Integer projectId, @Valid @RequestBody ProjectRequestDto projectRequestDto) {
    return new ResponseEntity<>(
        projectService.updateProject(projectId, projectRequestDto), HttpStatus.OK);
  }

  @PatchMapping("/{projectId}/collaborators/{userId}")
  public ResponseEntity<DetailedProjectDto> addProjectMember(
      @PathVariable Integer projectId, @PathVariable Integer userId) {
    projectService.addProjectMember(projectId, userId);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @DeleteMapping("/{projectId}/collaborators/{userId}")
  public ResponseEntity<DetailedProjectDto> removeProjectMember(
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
