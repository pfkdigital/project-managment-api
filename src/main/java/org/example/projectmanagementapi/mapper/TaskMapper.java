package org.example.projectmanagementapi.mapper;

import org.example.projectmanagementapi.dto.response.DetailedTaskDto;
import org.example.projectmanagementapi.dto.response.NestedTaskDto;
import org.example.projectmanagementapi.dto.response.TaskDto;
import org.example.projectmanagementapi.entity.Task;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TaskMapper {

  DetailedTaskDto toDetailedTaskDto(Task task);

  NestedTaskDto toNestedTaskDto(Task task);

  TaskDto toTaskDto(Task task);
}
