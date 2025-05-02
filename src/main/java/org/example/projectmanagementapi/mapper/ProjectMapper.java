package org.example.projectmanagementapi.mapper;

import org.example.projectmanagementapi.dto.response.DetailedProjectDto;
import org.example.projectmanagementapi.dto.response.ProjectWithCollaboratorsDto;
import org.example.projectmanagementapi.dto.response.NestedProjectDto;
import org.example.projectmanagementapi.entity.Project;
import org.mapstruct.*;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    uses = {UserMapper.class, TaskMapper.class})
public interface ProjectMapper {

  ProjectWithCollaboratorsDto toProjectWithCollaborators(Project entity);

  DetailedProjectDto toDetailedProjectDto(Project entity);

  NestedProjectDto toDto(Project project);
}
