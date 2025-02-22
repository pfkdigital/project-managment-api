package org.example.projectmanagementapi.mapper;

import org.example.projectmanagementapi.dto.TaskDto;
import org.example.projectmanagementapi.entity.Task;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TaskMapper {
  TaskMapper INSTANCE = Mappers.getMapper(TaskMapper.class);

  TaskDto taskToTaskDto(Task task);

  Task taskDtoToTask(TaskDto taskDto);
}
