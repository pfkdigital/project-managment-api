package org.example.projectmanagementapi.controller;

import lombok.RequiredArgsConstructor;
import org.example.projectmanagementapi.dto.TaskDto;
import org.example.projectmanagementapi.service.impl.TaskServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class TaskController {
  private final TaskServiceImpl taskService;

  @PostMapping()
  public ResponseEntity<?> createTask(@RequestBody TaskDto taskDto) {
    return new ResponseEntity<>(taskService.createTask(taskDto), HttpStatus.CREATED);
  }

  @GetMapping()
  public ResponseEntity<?> getTasks() {
    return new ResponseEntity<>(taskService.getTasks(), HttpStatus.OK);
  }

  @GetMapping("/{taskId}")
  public ResponseEntity<?> getTask(@PathVariable Integer taskId) {
    return new ResponseEntity<>(taskService.getTask(taskId), HttpStatus.OK);
  }

  @PutMapping("/{taskId}")
  public ResponseEntity<?> updateTask(@RequestBody TaskDto taskDto, @PathVariable Integer taskId) {
    return new ResponseEntity<>(taskService.updateTask(taskId, taskDto), HttpStatus.OK);
  }

  @DeleteMapping("/{taskId}")
  public ResponseEntity<?> deleteTask(@PathVariable Integer taskId) {
    taskService.deleteTask(taskId);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
