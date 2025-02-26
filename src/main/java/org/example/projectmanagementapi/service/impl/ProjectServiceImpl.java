package org.example.projectmanagementapi.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
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
import org.example.projectmanagementapi.service.ProjectService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

  private final ProjectMapper projectMapper;
  private final ProjectRepository projectRepository;
  private final NotificationService notificationService;
  private final UserRepository userRepository;

  @Override
  public ProjectWithUsersDto createProject(ProjectRequestDto projectRequestDto) {
    User owner = findUserById(projectRequestDto.getOwnerId());
    Project newProject =
        Project.builder()
            .name(projectRequestDto.getName())
            .description(projectRequestDto.getDescription())
            .displayImageUrl(projectRequestDto.getDisplayImageUrl())
            .status(ProjectStatus.ACTIVE)
            .build();

    newProject.addUser(owner);
    owner.addOwnedProject(newProject);
    owner.addProject(newProject);

    Project savedProject = projectRepository.save(newProject);

    notificationService.createNotification(
        "Project " + savedProject.getName() + " has been created", NotificationType.CREATION);

    return projectMapper.toProjectWithUsersDto(savedProject);
  }

  @Override
  public DetailedProjectDto getProjectById(Integer projectId) {
    Project selectedProject = findProjectById(projectId);

    return projectMapper.toDetailedProjectDto(selectedProject);
  }

  @Override
  public List<ProjectWithUsersDto> getAllProjects() {
    List<Project> projects = projectRepository.findAll();

    return projects.stream().map(projectMapper::toProjectWithUsersDto).toList();
  }

  @Override
  public DetailedProjectDto updateProject(Integer projectId, ProjectRequestDto projectRequestDto) {

    User owner = findUserById(projectRequestDto.getOwnerId());

    Project selectedProject = findProjectById(projectId);
    selectedProject.setName(projectRequestDto.getName());
    selectedProject.setDescription(projectRequestDto.getDescription());
    selectedProject.setDisplayImageUrl(projectRequestDto.getDisplayImageUrl());
    selectedProject.setStatus(projectRequestDto.getStatus());
    owner.addProject(selectedProject);

    Project updatedProject = projectRepository.save(selectedProject);

    notificationService.createNotification(
        "Project " + updatedProject.getName() + " has been updated", NotificationType.UPDATE);

    return projectMapper.toDetailedProjectDto(updatedProject);
  }

  @Override
  public void deleteProject(Integer projectId) {
    Project selectedProject = findProjectById(projectId);

    notificationService.createNotification(
        "Project " + selectedProject.getName() + " has been deleted", NotificationType.DESTRUCTION);

    projectRepository.delete(selectedProject);
  }

  @Override
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
    return projectRepository
        .findById(projectId)
        .orElseThrow(() -> new RuntimeException("Project not found of id " + projectId));
  }

  private User findUserById(Integer userId) {
    return userRepository
        .findById(userId)
        .orElseThrow(() -> new RuntimeException("User not found of id " + userId));
  }
}
