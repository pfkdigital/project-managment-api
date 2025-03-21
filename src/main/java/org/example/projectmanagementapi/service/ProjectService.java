package org.example.projectmanagementapi.service;

import java.util.List;

import org.example.projectmanagementapi.dto.response.DetailedProjectDto;
import org.example.projectmanagementapi.dto.request.ProjectRequestDto;
import org.example.projectmanagementapi.dto.response.ProjectWithCollaboratorsDto;

public interface ProjectService {
  ProjectWithCollaboratorsDto createProject(ProjectRequestDto projectRequestDto);

  DetailedProjectDto getProjectById(Integer projectId);

  List<ProjectWithCollaboratorsDto> getAllProjects();

  DetailedProjectDto updateProject(Integer projectId, ProjectRequestDto projectRequestDto);

  void deleteProject(Integer projectId);

  void addProjectMember(Integer projectId, Integer userId);

  void removeProjectMember(Integer projectId, Integer userId);
}
