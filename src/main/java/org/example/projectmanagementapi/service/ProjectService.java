package org.example.projectmanagementapi.service;

import java.util.List;

import org.example.projectmanagementapi.dto.response.DetailedProjectDto;
import org.example.projectmanagementapi.dto.request.ProjectRequestDto;
import org.example.projectmanagementapi.dto.response.ProjectWithUsersDto;

public interface ProjectService {
  ProjectWithUsersDto createProject(ProjectRequestDto projectRequestDto);

  DetailedProjectDto getProjectById(Integer projectId);

  List<ProjectWithUsersDto> getAllProjects();

  DetailedProjectDto updateProject(Integer projectId, ProjectRequestDto projectRequestDto);

  void deleteProject(Integer projectId);

  void addProjectMember(Integer projectId, Integer userId);

  void removeProjectMember(Integer projectId, Integer userId);
}
