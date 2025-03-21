package org.example.projectmanagementapi.service;

import java.util.List;

import org.example.projectmanagementapi.dto.response.DetailedTaskDto;
import org.example.projectmanagementapi.dto.response.TaskDto;
import org.example.projectmanagementapi.dto.request.TaskRequestDto;

public interface TaskService {
  TaskDto createTask(TaskRequestDto taskDto);

  DetailedTaskDto updateTask(Integer taskId, TaskRequestDto taskDto);

  void deleteTask(Integer taskId);

  DetailedTaskDto getTask(Integer taskId);

  List<TaskDto> getTasks();

  void assignUserToTask(Integer taskId, Integer userId);

  void removeUserFromTask(Integer taskId, Integer userId);
}
