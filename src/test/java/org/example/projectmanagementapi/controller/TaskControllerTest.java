package org.example.projectmanagementapi.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;
import org.example.projectmanagementapi.dto.request.TaskRequestDto;
import org.example.projectmanagementapi.dto.response.DetailedTaskDto;
import org.example.projectmanagementapi.dto.response.TaskDto;
import org.example.projectmanagementapi.service.impl.TaskServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class TaskControllerTest {

    @Mock private TaskServiceImpl taskService;

    @InjectMocks private TaskController taskController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateTask() {
        TaskDto taskDto = new TaskDto();
        when(taskService.createTask(any(TaskRequestDto.class))).thenReturn(taskDto);

        ResponseEntity<?> response = taskController.createTask(new TaskRequestDto());

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(taskDto, response.getBody());
    }

    @Test
    void testGetTasks() {
        List<TaskDto> tasks = Collections.emptyList();
        when(taskService.getTasks()).thenReturn(tasks);

        ResponseEntity<?> response = taskController.getTasks();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(tasks, response.getBody());
    }

    @Test
    void testGetTask() {
    DetailedTaskDto taskDto = new DetailedTaskDto();
        when(taskService.getTask(anyInt())).thenReturn(taskDto);

        ResponseEntity<?> response = taskController.getTask(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(taskDto, response.getBody());
    }

    @Test
    void testUpdateTask() {
    DetailedTaskDto taskDto = new DetailedTaskDto();
        when(taskService.updateTask(anyInt(), any(TaskRequestDto.class))).thenReturn(taskDto);

        ResponseEntity<?> response = taskController.updateTask(new TaskRequestDto(), 1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(taskDto, response.getBody());
    }

    @Test
    void testDeleteTask() {
        doNothing().when(taskService).deleteTask(anyInt());

        ResponseEntity<?> response = taskController.deleteTask(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(taskService, times(1)).deleteTask(1);
    }
}