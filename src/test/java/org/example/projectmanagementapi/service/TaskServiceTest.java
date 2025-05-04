package org.example.projectmanagementapi.service;

import org.example.projectmanagementapi.dto.request.TaskRequestDto;
import org.example.projectmanagementapi.dto.response.DetailedTaskDto;
import org.example.projectmanagementapi.dto.response.TaskDto;
import org.example.projectmanagementapi.entity.Project;
import org.example.projectmanagementapi.entity.Task;
import org.example.projectmanagementapi.entity.User;
import org.example.projectmanagementapi.enums.PriorityStatus;
import org.example.projectmanagementapi.enums.TaskStatus;
import org.example.projectmanagementapi.mapper.TaskMapper;
import org.example.projectmanagementapi.repository.ProjectRepository;
import org.example.projectmanagementapi.repository.TaskRepository;
import org.example.projectmanagementapi.repository.UserRepository;
import org.example.projectmanagementapi.service.NotificationService;
import org.example.projectmanagementapi.service.impl.TaskServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private TaskMapper taskMapper;

    @InjectMocks
    private TaskServiceImpl taskService;

    private TaskRequestDto taskRequestDto;
    private Task task;
    private Project project;
    private User user;
    private TaskDto taskDto;
    private DetailedTaskDto detailedTaskDto;

    @BeforeEach
    void setUp() {
        taskRequestDto = TaskRequestDto.builder()
                .description("Test Task")
                .dueDate(LocalDate.now().plusDays(7))
                .priority(PriorityStatus.HIGH)
                .projectId(1)
                .build();

        project = Project.builder()
                .id(1)
                .build();

        task = Task.builder()
                .id(1)
                .description("Test Task")
                .dueDate(LocalDate.now().plusDays(7))
                .priority(PriorityStatus.HIGH)
                .status(TaskStatus.OPEN)
                .project(project)
                .build();

        user = User.builder()
                .id(1)
                .build();

        taskDto = TaskDto.builder()
                .id(1)
                .description("Test Task")
                .build();

        detailedTaskDto = DetailedTaskDto.builder()
                .id(1)
                .description("Test Task")
                .build();
    }

    @Test
    void createTask_Success() {
        when(projectRepository.findById(1)).thenReturn(Optional.of(project));
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        when(taskMapper.toTaskDto(any(Task.class))).thenReturn(taskDto);

        TaskDto result = taskService.createTask(taskRequestDto);

        assertNotNull(result);
        assertEquals(taskDto.getId(), result.getId());
        assertEquals(taskDto.getDescription(), result.getDescription());

        verify(projectRepository).findById(1);
        verify(taskRepository).save(any(Task.class));
        verify(notificationService).createNotification(any());
    }

    @Test
    void createTask_ProjectNotFound() {
        when(projectRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> taskService.createTask(taskRequestDto));

        verify(taskRepository, never()).save(any());
        verify(notificationService, never()).createNotification(any());
    }

    @Test
    void getTask_Success() {
        when(taskRepository.findById(1)).thenReturn(Optional.of(task));
        when(taskRepository.findTaskByIdWithUsers(1)).thenReturn(Optional.of(task));
        when(taskRepository.findTaskByIdWithAttachments(1)).thenReturn(Optional.of(task));
        when(taskRepository.findTaskByIdWithComments(1)).thenReturn(Optional.of(task));
        when(taskMapper.toDetailedTaskDto(any(Task.class))).thenReturn(detailedTaskDto);

        DetailedTaskDto result = taskService.getTask(1);

        assertNotNull(result);
        assertEquals(detailedTaskDto.getId(), result.getId());
        assertEquals(detailedTaskDto.getDescription(), result.getDescription());
    }

    @Test
    void getTask_NotFound() {
        when(taskRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> taskService.getTask(1));
    }

    @Test
    void getTasks_Success() {
        List<Task> tasks = Arrays.asList(task);
        when(taskRepository.findAll()).thenReturn(tasks);
        when(taskMapper.toTaskDto(any(Task.class))).thenReturn(taskDto);

        List<TaskDto> result = taskService.getTasks();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(taskDto.getId(), result.get(0).getId());
    }

    @Test
    void assignUserToTask_Success() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(taskRepository.findById(1)).thenReturn(Optional.of(task));
        when(taskRepository.findTaskByIdWithUsers(1)).thenReturn(Optional.of(task));
        when(taskRepository.findTaskByIdWithAttachments(1)).thenReturn(Optional.of(task));
        when(taskRepository.findTaskByIdWithComments(1)).thenReturn(Optional.of(task));

        assertDoesNotThrow(() -> taskService.assignUserToTask(1, 1));

        verify(userRepository).findById(1);
        verify(taskRepository).findById(1);
    }

    @Test
    void assignUserToTask_UserNotFound() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> taskService.assignUserToTask(1, 1));

        verify(taskRepository, never()).findById(any());
    }

    @Test
    void updateTask_Success() {
        when(taskRepository.findById(1)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        when(taskMapper.toDetailedTaskDto(any(Task.class))).thenReturn(detailedTaskDto);

        DetailedTaskDto result = taskService.updateTask(1, taskRequestDto);

        assertNotNull(result);
        assertEquals(detailedTaskDto.getId(), result.getId());

        verify(taskRepository).findById(1);
        verify(taskRepository).save(any(Task.class));
        verify(notificationService).createNotification(any());
    }

    @Test
    void deleteTask_Success() {
        when(taskRepository.findById(1)).thenReturn(Optional.of(task));

        assertDoesNotThrow(() -> taskService.deleteTask(1));

        verify(taskRepository).findById(1);
        verify(taskRepository).delete(task);
        verify(notificationService).createNotification(any());
    }
}