package org.example.projectmanagementapi.service;

import org.example.projectmanagementapi.dto.CreateProjectDto;
import org.example.projectmanagementapi.dto.UpdateProjectDto;
import org.example.projectmanagementapi.entity.Project;
import org.example.projectmanagementapi.entity.User;

import java.util.List;

public interface ProjectService {
    Project createProject(CreateProjectDto createProjectDto);
    Project getProjectById(Integer projectId);
    List<Project> getAllProjects();
    Project updateProject(Integer projectId, UpdateProjectDto updateProjectDto);
    void deleteProject(Integer projectId);
    List<User> getProjectMembers(Integer projectId);
    void addProjectMember(Integer projectId, Integer userId);
    void removeProjectMember(Integer projectId, Integer userId);
}
