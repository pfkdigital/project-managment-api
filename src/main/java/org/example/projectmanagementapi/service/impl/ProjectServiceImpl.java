package org.example.projectmanagementapi.service.impl;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
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
import org.example.projectmanagementapi.service.NotificationService;
import org.example.projectmanagementapi.service.ProjectService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

  private final ProjectMapper projectMapper;
  private final ProjectRepository projectRepository;
  private final NotificationService notificationService;
  private final UserRepository userRepository;

  @Override
  public ProjectWithCollaboratorsDto createProject(ProjectRequestDto projectRequestDto) {
    User owner = findUserById(projectRequestDto.getOwnerId());
    Project newProject =
        Project.builder()
            .name(projectRequestDto.getName())
            .description(projectRequestDto.getDescription())
            .displayImageUrl(projectRequestDto.getDisplayImageUrl())
            .status(ProjectStatus.ACTIVE)
            .build();

    owner.addOwnedProject(newProject);

    Project savedProject = projectRepository.save(newProject);

    notificationService.createNotification(
        "Project " + savedProject.getName() + " has been created", NotificationType.CREATION);

    return projectMapper.toProjectWithCollaborators(savedProject);
  }

  @Override
  @CachePut(value = "projects", key = "#projectId")
  public DetailedProjectDto getProjectById(Integer projectId) {
    Project selectedProject = findProjectById(projectId);

    return projectMapper.toDetailedProjectDto(selectedProject);
  }

  @Override
  @Cacheable(value = "projects")
  public List<ProjectWithCollaboratorsDto> getAllProjects() {
    List<Project> projects = projectRepository.findAll();

    return projects.stream().map(projectMapper::toProjectWithCollaborators).toList();
  }

  @Override
  @CachePut(value = "projects", key = "#projectId")
  public DetailedProjectDto updateProject(Integer projectId, ProjectRequestDto projectRequestDto) {

    Project selectedProject =
        projectRepository
            .findById(projectId)
            .orElseThrow(() -> new RuntimeException("Project not found with id " + projectId));
    selectedProject.setName(projectRequestDto.getName());
    selectedProject.setDescription(projectRequestDto.getDescription());
    selectedProject.setDisplayImageUrl(projectRequestDto.getDisplayImageUrl());
    selectedProject.setStatus(projectRequestDto.getStatus());

    User owner = findUserById(projectRequestDto.getOwnerId());

    if (selectedProject.getOwner().getId() != projectRequestDto.getOwnerId()) {
      selectedProject.removeUser(selectedProject.getOwner());
      selectedProject.getOwner().removeOwnedProject(selectedProject);
      owner.addOwnedProject(selectedProject);
      selectedProject.addUser(owner);
    }

    Project updatedProject = projectRepository.save(selectedProject);

    notificationService.createNotification(
        "Project " + updatedProject.getName() + " has been updated", NotificationType.UPDATE);

    return projectMapper.toDetailedProjectDto(updatedProject);
  }

  @Override
  @CacheEvict(value = "projects", key = "#projectId")
  public void deleteProject(Integer projectId) {
    Project selectedProject =
        projectRepository
            .findById(projectId)
            .orElseThrow(() -> new RuntimeException("Project not found with id " + projectId));
    projectRepository.delete(selectedProject);
    notificationService.createNotification(
        "Project " + selectedProject.getName() + " has been deleted", NotificationType.DESTRUCTION);
  }

  @Override
  @CachePut(value = "projects", key = "#projectId")
  public void addProjectMember(Integer projectId, Integer userId) {
    Project selectedProject = findProjectById(projectId);
    User selectedUser = findUserById(userId);

    selectedProject.addUser(selectedUser);
    projectRepository.save(selectedProject);

    notificationService.createNotification(
        "User "
            + selectedUser.getUsername()
            + " has been added to project "
            + selectedProject.getName(),
        NotificationType.CREATION);
  }

  @Override
  @CachePut(value = "projects", key = "#projectId")
  public void removeProjectMember(Integer projectId, Integer userId) {
    Project selectedProject = findProjectById(projectId);
    User selectedUser = findUserById(userId);

    selectedProject.removeUser(selectedUser);
    projectRepository.save(selectedProject);

    notificationService.createNotification(
        "User "
            + selectedUser.getUsername()
            + " has been removed from project "
            + selectedProject.getName(),
        NotificationType.DESTRUCTION);
  }

  private Project findProjectById(Integer projectId) {
    Project project =
        projectRepository
            .findById(projectId)
            .orElseThrow(() -> new RuntimeException("Project not found with id " + projectId));

    project.setOwner(
        projectRepository.findProjectWithOwnerById(projectId).map(Project::getOwner).orElse(null));

    project.setCollaborators(
        projectRepository
            .findProjectWithCollaboratorsById(projectId)
            .map(Project::getCollaborators)
            .orElse(new ArrayList<>()));

    project.setTasks(
        projectRepository
            .findProjectWithTasksById(projectId)
            .map(Project::getTasks)
            .orElse(new ArrayList<>()));

    project.setIssues(
        projectRepository
            .findProjectWithIssuesById(projectId)
            .map(Project::getIssues)
            .orElse(new ArrayList<>()));

    return project;
  }

  private User findUserById(Integer userId) {
    return userRepository
        .findById(userId)
        .orElseThrow(() -> new RuntimeException("User not found of id " + userId));
  }
}
