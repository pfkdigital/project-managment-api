package org.example.projectmanagementapi.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.projectmanagementapi.dto.ProjectDto;
import org.example.projectmanagementapi.entity.Notification;
import org.example.projectmanagementapi.entity.Project;
import org.example.projectmanagementapi.entity.User;
import org.example.projectmanagementapi.enums.NotificationType;
import org.example.projectmanagementapi.enums.ProjectStatus;
import org.example.projectmanagementapi.repository.ProjectRepository;
import org.example.projectmanagementapi.repository.UserRepository;
import org.example.projectmanagementapi.service.NotificationService;
import org.example.projectmanagementapi.service.ProjectService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final NotificationService notificationService;
    private final UserRepository userRepository;

    @Override
    public Project createProject(ProjectDto projectDto) {
        User owner = findUserById(projectDto.getOwnerId());
        Project newProject = Project.builder()
                .name(projectDto.getName())
                .description(projectDto.getDescription())
                .displayImageUrl(projectDto.getDisplayImageUrl())
                .status(ProjectStatus.ACTIVE)
                .build();
        owner.addProject(newProject);

        Project savedProject = projectRepository.save(newProject);

        createNotification("Project " + savedProject.getName() + " has been created", NotificationType.CREATION);

        return savedProject;
    }

    @Override
    public Project getProjectById(Integer projectId) {
        return findProjectById(projectId);
    }

    @Override
    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    @Override
    public Project updateProject(Integer projectId, ProjectDto projectDto) {

        User owner = findUserById(projectDto.getOwnerId());

        Project selectedProject = findProjectById(projectId);
        selectedProject.setName(projectDto.getName());
        selectedProject.setDescription(projectDto.getDescription());
        selectedProject.setDisplayImageUrl(projectDto.getDisplayImageUrl());
        selectedProject.setStatus(projectDto.getStatus());
        owner.addProject(selectedProject);

        Project updatedProject = projectRepository.save(selectedProject);

        createNotification("Project " + updatedProject.getName() + " has been updated", NotificationType.UPDATE);

        return updatedProject;
    }

    @Override
    public void deleteProject(Integer projectId) {
        Project selectedProject = findProjectById(projectId);

        createNotification("Project " + selectedProject.getName() + " has been deleted", NotificationType.DESTRUCTION);

        projectRepository.delete(selectedProject);
    }

    @Override
    public List<User> getProjectMembers(Integer projectId) {
        Project selectedProject = findProjectById(projectId);
        return selectedProject.getUsers();
    }

    @Override
    public void addProjectMember(Integer projectId, Integer userId) {
        Project selectedProject = findProjectById(projectId);
        User selectedUser = findUserById(userId);

        selectedProject.addUser(selectedUser);
        projectRepository.save(selectedProject);

        createNotification("User " + selectedUser.getUsername() + " has been added to project " + selectedProject.getName(), NotificationType.CREATION);
    }

    @Override
    public void removeProjectMember(Integer projectId, Integer userId) {
        Project selectedProject = findProjectById(projectId);
        User selectedUser = findUserById(userId);

        selectedProject.removeUser(selectedUser);
        projectRepository.save(selectedProject);

        createNotification("User " + selectedUser.getUsername() + " has been removed from project " + selectedProject.getName(), NotificationType.DESTRUCTION);
    }

    private Project findProjectById(Integer projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found of id " + projectId));
    }

    private User findUserById(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found of id " + userId));
    }

    private void createNotification(String message, NotificationType type) {
        Notification notification = Notification.builder()
                .message(message)
                .type(type)
                .isRead(false)
                .build();
        notificationService.createNotification(notification);
    }
}