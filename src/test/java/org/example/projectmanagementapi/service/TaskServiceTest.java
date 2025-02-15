package org.example.projectmanagementapi.service;

import org.example.projectmanagementapi.dto.TaskDto;
import org.example.projectmanagementapi.dto.UserDto;
import org.example.projectmanagementapi.entity.Notification;
import org.example.projectmanagementapi.entity.Project;
import org.example.projectmanagementapi.entity.Task;
import org.example.projectmanagementapi.enums.PriorityStatus;
import org.example.projectmanagementapi.enums.TaskStatus;
import org.example.projectmanagementapi.repository.ProjectRepository;
import org.example.projectmanagementapi.repository.TaskRepository;
import org.example.projectmanagementapi.service.impl.TaskServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private TaskServiceImpl taskService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createTask_createsAndReturnsTaskWithNotification() {
        TaskDto taskDto = new TaskDto("Description", LocalDate.now().plusDays(1), PriorityStatus.HIGH, TaskStatus.OPEN, List.of(new UserDto()), 1);
        Project project = new Project();
        Task task = Task.builder().id(1).description("Description").build();

        when(projectRepository.findById(1)).thenReturn(Optional.of(project));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        Task createdTask = taskService.createTask(taskDto);

        assertNotNull(createdTask);
        assertEquals("Description", createdTask.getDescription());
        verify(projectRepository, times(1)).findById(1);
        verify(taskRepository, times(1)).save(any(Task.class));
        verify(notificationService, times(1)).createNotification(any(Notification.class));
    }

    @Test
    void createTask_throwsExceptionWhenProjectNotFound() {
        TaskDto taskDto = new TaskDto("Description", LocalDate.now().plusDays(1), PriorityStatus.HIGH, TaskStatus.OPEN, List.of(new UserDto()), 1);

        when(projectRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> taskService.createTask(taskDto));
        verify(projectRepository, times(1)).findById(1);
        verify(taskRepository, never()).save(any(Task.class));
        verify(notificationService, never()).createNotification(any(Notification.class));
    }

    @Test
    void updateTask_updatesAndReturnsTaskWithNotification() {
        TaskDto taskDto = new TaskDto("Updated Description", LocalDate.now().plusDays(1), PriorityStatus.MEDIUM, TaskStatus.IN_PROGRESS, List.of(new UserDto()), 1);
        Task task = Task.builder().id(1).description("Description").build();

        when(taskRepository.findById(1)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        Task updatedTask = taskService.updateTask(1, taskDto);

        assertNotNull(updatedTask);
        assertEquals("Updated Description", updatedTask.getDescription());
        verify(taskRepository, times(1)).findById(1);
        verify(taskRepository, times(1)).save(any(Task.class));
        verify(notificationService, times(1)).createNotification(any(Notification.class));
    }

    @Test
    void updateTask_throwsExceptionWhenTaskNotFound() {
        TaskDto taskDto = new TaskDto("Updated Description", LocalDate.now().plusDays(1), PriorityStatus.MEDIUM, TaskStatus.IN_PROGRESS, List.of(new UserDto()), 1);

        when(taskRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> taskService.updateTask(1, taskDto));
        verify(taskRepository, times(1)).findById(1);
        verify(taskRepository, never()).save(any(Task.class));
        verify(notificationService, never()).createNotification(any(Notification.class));
    }

    @Test
    void deleteTask_deletesTaskWithNotification() {
        Task task = Task.builder().id(1).description("Description").build();

        when(taskRepository.findById(1)).thenReturn(Optional.of(task));

        taskService.deleteTask(1);

        verify(taskRepository, times(1)).findById(1);
        verify(taskRepository, times(1)).delete(any(Task.class));
        verify(notificationService, times(1)).createNotification(any(Notification.class));
    }

    @Test
    void deleteTask_throwsExceptionWhenTaskNotFound() {
        when(taskRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> taskService.deleteTask(1));
        verify(taskRepository, times(1)).findById(1);
        verify(taskRepository, never()).delete(any(Task.class));
        verify(notificationService, never()).createNotification(any(Notification.class));
    }

    @Test
    void getTask_returnsTaskWithAttachmentsAndComments() {
        Task task = Task.builder().id(1).description("Description").build();

        when(taskRepository.getTaskByIdWithAttachmentAndComments(1)).thenReturn(Optional.of(task));

        Task foundTask = taskService.getTask(1);

        assertNotNull(foundTask);
        assertEquals("Description", foundTask.getDescription());
        verify(taskRepository, times(1)).getTaskByIdWithAttachmentAndComments(1);
    }

    @Test
    void getTask_throwsExceptionWhenTaskNotFound() {
        when(taskRepository.getTaskByIdWithAttachmentAndComments(1)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> taskService.getTask(1));
        verify(taskRepository, times(1)).getTaskByIdWithAttachmentAndComments(1);
    }

    @Test
    void getTasks_returnsAllTasks() {
        List<Task> tasks = List.of(Task.builder().id(1).description("Description1").build(), Task.builder().id(2).description("Description2").build());

        when(taskRepository.findAll()).thenReturn(tasks);

        List<Task> foundTasks = taskService.getTasks();

        assertNotNull(foundTasks);
        assertEquals(2, foundTasks.size());
        verify(taskRepository, times(1)).findAll();
    }
}