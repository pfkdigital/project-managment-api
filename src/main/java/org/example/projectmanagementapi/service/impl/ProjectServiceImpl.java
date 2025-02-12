package org.example.projectmanagementapi.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.projectmanagementapi.dto.CreateProjectDto;
import org.example.projectmanagementapi.dto.UpdateProjectDto;
import org.example.projectmanagementapi.entity.Project;
import org.example.projectmanagementapi.entity.User;
import org.example.projectmanagementapi.enums.ProjectStatus;
import org.example.projectmanagementapi.repository.ProjectRepository;
import org.example.projectmanagementapi.repository.UserRepository;
import org.example.projectmanagementapi.service.ProjectService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    @Override
    public Project createProject(CreateProjectDto createProjectDto) {
        Project newProject = Project.builder()
                .name(createProjectDto.name())
                .description(createProjectDto.description())
                .displayImageUrl(createProjectDto.displayImageUrl())
                .status(ProjectStatus.ACTIVE)
                .owner(createProjectDto.owner())
                .build();
        return projectRepository.save(newProject);
    }

    @Override
    public Project getProjectById(Integer projectId) {
        return projectRepository.findById(projectId).orElseThrow(() -> new RuntimeException("Project not found of id " + projectId));
    }

    @Override
    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    @Override
    public Project updateProject(Integer projectId, UpdateProjectDto updateProjectDto) {

        Project selectedProject = projectRepository.findById(projectId).orElseThrow(() -> new RuntimeException("Project not found of id " + projectId));
        selectedProject.setName(updateProjectDto.name());
        selectedProject.setDescription(updateProjectDto.description());
        selectedProject.setDisplayImageUrl(updateProjectDto.displayImageUrl());
        selectedProject.setStatus(updateProjectDto.status());
        return projectRepository.save(selectedProject);
    }

    @Override
    public void deleteProject(Integer projectId) {

        Project selectedProject = projectRepository.findById(projectId).orElseThrow(() -> new RuntimeException("Project not found of id " + projectId));
        projectRepository.delete(selectedProject);
    }

    @Override
    public List<User> getProjectMembers(Integer projectId) {
        Project selectedProject = projectRepository.findById(projectId).orElseThrow(() -> new RuntimeException("Project not found of id " + projectId));

        return selectedProject.getUsers();
    }

    @Override
    public void addProjectMember(Integer projectId, Integer userId) {
        Project selectedProject = projectRepository.findById(projectId).orElseThrow(() -> new RuntimeException("Project not found of id " + projectId));
        User selectedUser = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found of id " + userId));

        selectedProject.getUsers().add(selectedUser);

        projectRepository.save(selectedProject);
    }

    @Override
    public void removeProjectMember(Integer projectId, Integer userId) {
        Project selectedProject = projectRepository.findById(projectId).orElseThrow(() -> new RuntimeException("Project not found of id " + projectId));
        User selectedUser = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found of id " + userId));

        selectedProject.getUsers().remove(selectedUser);

        projectRepository.save(selectedProject);
    }
}
