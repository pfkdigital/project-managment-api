package org.example.projectmanagementapi.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.projectmanagementapi.dto.TaskDto;
import org.example.projectmanagementapi.entity.Notification;
import org.example.projectmanagementapi.entity.Project;
import org.example.projectmanagementapi.entity.Task;
import org.example.projectmanagementapi.enums.NotificationType;
import org.example.projectmanagementapi.repository.ProjectRepository;
import org.example.projectmanagementapi.repository.TaskRepository;
import org.example.projectmanagementapi.service.NotificationService;
import org.example.projectmanagementapi.service.TaskService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final NotificationService notificationService;


    @Override
    public Task createTask(TaskDto taskDto) {
        Project project = projectRepository.findById(taskDto.getProjectId())
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        Task task = Task.builder()
                .description(taskDto.getDescription())
                .dueDate(taskDto.getDueDate())
                .priority(taskDto.getPriority())
                .build();

        project.addTask(task);

        Task newTask = taskRepository.save(task);

        Notification notification = Notification.builder()
                .message("Task with id" + newTask.getId() + " has been created")
                .type(NotificationType.CREATION)
                .isRead(false)
                .build();

        notificationService.createNotification(notification);

        return newTask;
    }

    @Override
    public Task getTask(Integer taskId) {
        return taskRepository.getTaskByIdWithAttachmentAndComments(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found with id " + taskId));
    }

    @Override
    public List<Task> getTasks() {
        return taskRepository.findAll();
    }

    @Override
    public Task updateTask(Integer taskId, TaskDto taskDto) {
        Task selectedTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found with id " + taskId));

        selectedTask.setDescription(taskDto.getDescription());
        selectedTask.setDueDate(taskDto.getDueDate());
        selectedTask.setPriority(taskDto.getPriority());
        selectedTask.setStatus(taskDto.getStatus());

        Task updatedTask = taskRepository.save(selectedTask);

        Notification notification = Notification.builder()
                .message("Task with id " + updatedTask.getId() + " has been updated")
                .type(NotificationType.UPDATE)
                .isRead(false)
                .build();

        notificationService.createNotification(notification);
        return updatedTask;
    }

    @Override
    public void deleteTask(Integer taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found with id " + taskId));

        taskRepository.delete(task);

        Notification notification = Notification.builder()
                .message("Task with id " + taskId + " has been deleted")
                .type(NotificationType.DESTRUCTION)
                .isRead(false)
                .build();

        notificationService.createNotification(notification);
    }
}
