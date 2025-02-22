package org.example.projectmanagementapi.service;

import java.util.List;
import org.example.projectmanagementapi.dto.TaskDto;
import org.example.projectmanagementapi.entity.Task;

public interface TaskService {
  Task createTask(TaskDto taskDto);

  Task updateTask(Integer taskId, TaskDto taskDto);

  void deleteTask(Integer taskId);

  Task getTask(Integer taskId);

  List<Task> getTasks();
}
