//package org.example.projectmanagementapi.service;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//import java.time.LocalDate;
//import java.util.List;
//import java.util.Optional;
//
//import org.example.projectmanagementapi.dto.request.TaskRequestDto;
//import org.example.projectmanagementapi.dto.response.DetailedTaskDto;
//import org.example.projectmanagementapi.dto.response.TaskDto;
//import org.example.projectmanagementapi.dto.response.UserDto;
//import org.example.projectmanagementapi.entity.Notification;
//import org.example.projectmanagementapi.entity.Project;
//import org.example.projectmanagementapi.entity.Task;
//import org.example.projectmanagementapi.enums.PriorityStatus;
//import org.example.projectmanagementapi.enums.TaskStatus;
//import org.example.projectmanagementapi.mapper.TaskMapper;
//import org.example.projectmanagementapi.repository.ProjectRepository;
//import org.example.projectmanagementapi.repository.TaskRepository;
//import org.example.projectmanagementapi.service.impl.TaskServiceImpl;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//@ExtendWith(MockitoExtension.class)
//class TaskServiceTest {
//
//    @Mock private TaskRepository taskRepository;
//    @Mock private ProjectRepository projectRepository;
//    @Mock private NotificationService notificationService;
//    @Mock private TaskMapper taskMapper;
//
//    @InjectMocks private TaskServiceImpl taskService;
//
//    private Task task;
//    private Project project;
//    private TaskRequestDto taskRequestDto;
//
//    @BeforeEach
//    void setUp() {
//        project = new Project();
//        project.setId(1);
//
//        task = Task.builder()
//                .id(1)
//                .description("Description")
//                .dueDate(LocalDate.now().plusDays(1))
//                .priority(PriorityStatus.HIGH)
//                .status(TaskStatus.OPEN)
//                .build();
//
//        taskRequestDto = new TaskRequestDto(
//                "Description",
//                LocalDate.now().plusDays(1),
//                PriorityStatus.HIGH,
//                TaskStatus.OPEN,
//                List.of(new UserDto()),
//                1);
//    }
//
//    @Test
//    void createTask_createsAndReturnsTaskWithNotification() {
//        when(projectRepository.findById(1)).thenReturn(Optional.of(project));
//        when(taskRepository.save(any(Task.class))).thenReturn(task);
//        when(taskMapper.toTaskDto(any(Task.class))).thenReturn(new TaskDto());
//
//        TaskDto createdTask = taskService.createTask(taskRequestDto);
//
//        assertNotNull(createdTask);
//        verify(projectRepository, times(1)).findById(1);
//        verify(taskRepository, times(1)).save(any(Task.class));
//        verify(notificationService, times(1)).createNotification(any(Notification.class));
//    }
//
//    @Test
//    void createTask_throwsExceptionWhenProjectNotFound() {
//        when(projectRepository.findById(1)).thenReturn(Optional.empty());
//
//        assertThrows(IllegalArgumentException.class, () -> taskService.createTask(taskRequestDto));
//        verify(projectRepository, times(1)).findById(1);
//        verify(taskRepository, never()).save(any(Task.class));
//        verify(notificationService, never()).createNotification(any(Notification.class));
//    }
//
//    @Test
//    void updateTask_updatesAndReturnsTaskWithNotification() {
//        when(taskRepository.findById(1)).thenReturn(Optional.of(task));
//        when(taskRepository.save(any(Task.class))).thenReturn(task);
//        when(taskMapper.toDetailedTaskDto(any(Task.class))).thenReturn(new DetailedTaskDto());
//
//        DetailedTaskDto updatedTask = taskService.updateTask(1, taskRequestDto);
//
//        assertNotNull(updatedTask);
//        verify(taskRepository, times(1)).findById(1);
//        verify(taskRepository, times(1)).save(any(Task.class));
//        verify(notificationService, times(1)).createNotification(any(Notification.class));
//    }
//
//    @Test
//    void updateTask_throwsExceptionWhenTaskNotFound() {
//        when(taskRepository.findById(1)).thenReturn(Optional.empty());
//
//        assertThrows(IllegalArgumentException.class, () -> taskService.updateTask(1, taskRequestDto));
//        verify(taskRepository, times(1)).findById(1);
//        verify(taskRepository, never()).save(any(Task.class));
//        verify(notificationService, never()).createNotification(any(Notification.class));
//    }
//
//    @Test
//    void deleteTask_deletesTaskWithNotification() {
//        when(taskRepository.findById(1)).thenReturn(Optional.of(task));
//
//        taskService.deleteTask(1);
//
//        verify(taskRepository, times(1)).findById(1);
//        verify(taskRepository, times(1)).delete(any(Task.class));
//        verify(notificationService, times(1)).createNotification(any(Notification.class));
//    }
//
//    @Test
//    void deleteTask_throwsExceptionWhenTaskNotFound() {
//        when(taskRepository.findById(1)).thenReturn(Optional.empty());
//
//        assertThrows(IllegalArgumentException.class, () -> taskService.deleteTask(1));
//        verify(taskRepository, times(1)).findById(1);
//        verify(taskRepository, never()).delete(any(Task.class));
//        verify(notificationService, never()).createNotification(any(Notification.class));
//    }
//
//    @Test
//    void getTask_returnsTaskWithAttachmentsAndComments() {
//        when(taskRepository.findById(1)).thenReturn(Optional.of(task));
//        when(taskMapper.toDetailedTaskDto(any(Task.class))).thenReturn(new DetailedTaskDto());
//
//        DetailedTaskDto foundTask = taskService.getTask(1);
//
//        assertNotNull(foundTask);
//        verify(taskRepository, times(1)).findById(1);
//    }
//
//    @Test
//    void getTask_throwsExceptionWhenTaskNotFound() {
//        when(taskRepository.findById(1)).thenReturn(Optional.empty());
//
//        assertThrows(IllegalArgumentException.class, () -> taskService.getTask(1));
//        verify(taskRepository, times(1)).findById(1);
//    }
//
//    @Test
//    void getTasks_returnsAllTasks() {
//        List<Task> tasks = List.of(task);
//        when(taskRepository.findAll()).thenReturn(tasks);
//        when(taskMapper.toTaskDto(any(Task.class))).thenReturn(new TaskDto());
//
//        List<TaskDto> foundTasks = taskService.getTasks();
//
//        assertNotNull(foundTasks);
//        assertEquals(1, foundTasks.size());
//        verify(taskRepository, times(1)).findAll();
//    }
//}