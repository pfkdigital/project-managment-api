package org.example.projectmanagementapi.mapper;

import org.example.projectmanagementapi.dto.response.DetailedProjectDto;
import org.example.projectmanagementapi.dto.response.ProjectWithUsersDto;
import org.example.projectmanagementapi.dto.response.NestedProjectDto;
import org.example.projectmanagementapi.entity.Project;
import org.mapstruct.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProjectMapper {

  ProjectWithUsersDto toProjectWithUsersDto(Project entity);

  DetailedProjectDto toDetailedProjectDto(Project entity);

  Project toEntity(NestedProjectDto nestedProjectDto);

  NestedProjectDto toDto(Project project);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  Project partialUpdate(NestedProjectDto nestedProjectDto, @MappingTarget Project project);
}
