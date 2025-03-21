package org.example.projectmanagementapi.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.projectmanagementapi.dto.request.TaskRequestDto;
import org.example.projectmanagementapi.dto.response.DetailedTaskDto;
import org.example.projectmanagementapi.dto.response.TaskDto;
import org.example.projectmanagementapi.service.impl.TaskServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class TaskController {
  private final TaskServiceImpl taskService;

  @PostMapping()
  public ResponseEntity<TaskDto> createTask(@Valid @RequestBody TaskRequestDto taskDto) {
    return new ResponseEntity<>(taskService.createTask(taskDto), HttpStatus.CREATED);
  }

  @GetMapping()
  public ResponseEntity<List<TaskDto>> getTasks() {
    return new ResponseEntity<>(taskService.getTasks(), HttpStatus.OK);
  }

  @GetMapping("/{taskId}")
  public ResponseEntity<DetailedTaskDto> getTask(@PathVariable Integer taskId) {
    return new ResponseEntity<>(taskService.getTask(taskId), HttpStatus.OK);
  }

  @PutMapping("/{taskId}")
  public ResponseEntity<DetailedTaskDto> updateTask(
      @Valid @RequestBody TaskRequestDto taskDto, @PathVariable Integer taskId) {
    return new ResponseEntity<>(taskService.updateTask(taskId, taskDto), HttpStatus.OK);
  }

  @PutMapping("/{taskId}/users/{userId}")
  public ResponseEntity<?> assignUserToTask(
      @PathVariable Integer taskId, @PathVariable Integer userId) {
    taskService.assignUserToTask(taskId, userId);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @DeleteMapping("/{taskId}/users/{userId}")
  public ResponseEntity<?> removeUserFromTask(
      @PathVariable Integer taskId, @PathVariable Integer userId) {
    taskService.removeUserFromTask(taskId, userId);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @DeleteMapping("/{taskId}")
  public ResponseEntity<?> deleteTask(@PathVariable Integer taskId) {
    taskService.deleteTask(taskId);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
