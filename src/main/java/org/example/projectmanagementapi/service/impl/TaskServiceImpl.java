package org.example.projectmanagementapi.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.projectmanagementapi.dto.response.DetailedTaskDto;
import org.example.projectmanagementapi.dto.response.TaskDto;
import org.example.projectmanagementapi.dto.request.TaskRequestDto;
import org.example.projectmanagementapi.entity.Notification;
import org.example.projectmanagementapi.entity.Project;
import org.example.projectmanagementapi.entity.Task;
import org.example.projectmanagementapi.enums.NotificationType;
import org.example.projectmanagementapi.mapper.TaskMapper;
import org.example.projectmanagementapi.repository.ProjectRepository;
import org.example.projectmanagementapi.repository.TaskRepository;
import org.example.projectmanagementapi.service.NotificationService;
import org.example.projectmanagementapi.service.TaskService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

  private final TaskRepository taskRepository;
  private final ProjectRepository projectRepository;
  private final NotificationService notificationService;
  private final TaskMapper taskMapper;

  @Override
  public TaskDto createTask(TaskRequestDto taskDto) {
    Project project =
        projectRepository
            .findById(taskDto.getProjectId())
            .orElseThrow(() -> new IllegalArgumentException("Project not found with id " + taskDto.getProjectId()));

    Task task =
        Task.builder()
            .description(taskDto.getDescription())
            .dueDate(taskDto.getDueDate())
            .priority(taskDto.getPriority())
            .status(taskDto.getStatus())
            .build();

    project.addTask(task);

    Task newTask = taskRepository.save(task);

    Notification notification =
        Notification.builder()
            .message("Task with id" + newTask.getId() + " has been created")
            .type(NotificationType.CREATION)
            .isRead(false)
            .build();

    notificationService.createNotification(notification);

    return taskMapper.toTaskDto(newTask);
  }

  @Override
  public DetailedTaskDto getTask(Integer taskId) {
    Task selectedTask =
        taskRepository
            .findById(taskId)
            .orElseThrow(() -> new IllegalArgumentException("Task not found with id " + taskId));

    return taskMapper.toDetailedTaskDto(selectedTask);
  }

  @Override
  public List<TaskDto> getTasks() {
    List<Task> tasks = taskRepository.findAll();
    return tasks.stream().map(taskMapper::toTaskDto).toList();
  }

  @Override
  public DetailedTaskDto updateTask(Integer taskId, TaskRequestDto taskDto) {
    Task selectedTask =
        taskRepository
            .findById(taskId)
            .orElseThrow(() -> new IllegalArgumentException("Task not found with id " + taskId));

    selectedTask.setDescription(taskDto.getDescription());
    selectedTask.setDueDate(taskDto.getDueDate());
    selectedTask.setPriority(taskDto.getPriority());
    selectedTask.setStatus(taskDto.getStatus());

    Task updatedTask = taskRepository.save(selectedTask);

    Notification notification =
        Notification.builder()
            .message("Task with id " + updatedTask.getId() + " has been updated")
            .type(NotificationType.UPDATE)
            .isRead(false)
            .build();

    notificationService.createNotification(notification);
    return taskMapper.toDetailedTaskDto(updatedTask);
  }

  @Override
  public void deleteTask(Integer taskId) {
    Task task =
        taskRepository
            .findById(taskId)
            .orElseThrow(() -> new IllegalArgumentException("Task not found with id " + taskId));

    taskRepository.delete(task);

    Notification notification =
        Notification.builder()
            .message("Task with id " + taskId + " has been deleted")
            .type(NotificationType.DESTRUCTION)
            .isRead(false)
            .build();

    notificationService.createNotification(notification);
  }
}
