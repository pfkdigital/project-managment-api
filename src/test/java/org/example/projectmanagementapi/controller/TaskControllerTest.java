package org.example.projectmanagementapi.controller;

import org.example.projectmanagementapi.dto.TaskDto;
import org.example.projectmanagementapi.entity.Task;
import org.example.projectmanagementapi.service.impl.TaskServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

public class TaskControllerTest {

    @Mock
    private TaskServiceImpl taskService;

    @InjectMocks
    private TaskController taskController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateTask() {
        Task task = new Task();
        when(taskService.createTask(any(TaskDto.class))).thenReturn(task);

        ResponseEntity<?> response = taskController.createTask(new TaskDto());

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(task, response.getBody());
    }

    @Test
    void testGetTasks() {
        List<Task> tasks = Collections.emptyList();
        when(taskService.getTasks()).thenReturn(tasks);

        ResponseEntity<?> response = taskController.getTasks();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(tasks, response.getBody());
    }

    @Test
    void testGetTask() {
        Task task = new Task();
        when(taskService.getTask(anyInt())).thenReturn(task);

        ResponseEntity<?> response = taskController.getTask(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(task, response.getBody());
    }

    @Test
    void testUpdateTask() {
        Task task = new Task();
        when(taskService.updateTask(anyInt(), any(TaskDto.class))).thenReturn(task);

        ResponseEntity<?> response = taskController.updateTask(new TaskDto(), 1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(task, response.getBody());
    }

    @Test
    void testDeleteTask() {
        doNothing().when(taskService).deleteTask(anyInt());

        ResponseEntity<?> response = taskController.deleteTask(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(taskService, times(1)).deleteTask(1);
    }
}