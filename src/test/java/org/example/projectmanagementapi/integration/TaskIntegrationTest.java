package org.example.projectmanagementapi.integration;

import org.example.projectmanagementapi.dto.request.TaskRequestDto;
import org.example.projectmanagementapi.dto.response.DetailedTaskDto;
import org.example.projectmanagementapi.dto.response.TaskDto;
import org.example.projectmanagementapi.enums.PriorityStatus;
import org.example.projectmanagementapi.enums.TaskStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TaskIntegrationTest extends BaseIntegration {

  @Autowired private TestRestTemplate restTemplate;

  @LocalServerPort private int port;

  @Test
  @Sql({"/schema.sql", "/data.sql"})
  public void testCreateTask() {
    String url = "http://localhost:" + port + "/api/v1/tasks";

    TaskRequestDto taskRequestDto = new TaskRequestDto();
    taskRequestDto.setDescription("Test Task");
    taskRequestDto.setStatus(TaskStatus.OPEN);
    taskRequestDto.setPriority(PriorityStatus.HIGH);
    taskRequestDto.setDueDate(LocalDate.now().plusDays(7));
    taskRequestDto.setProjectId(1);

    TaskDto response = restTemplate.postForObject(url, taskRequestDto, TaskDto.class);
    System.out.println(response);
    assertNotNull(response);
    assertEquals("Test Task", response.getDescription());
  }

  @Test
  @Sql({"/schema.sql", "/data.sql"})
  public void testGetAllTasks() {
    String url = "http://localhost:" + port + "/api/v1/tasks";
    TaskDto[] tasks = restTemplate.getForObject(url, TaskDto[].class);

    assertNotNull(tasks);
    assertEquals(5, tasks.length);
    assertEquals("Develop login feature", tasks[0].getDescription());
  }

  @Test
  @Sql({"/schema.sql", "/data.sql"})
  public void testGetTaskById() {
    String url = "http://localhost:" + port + "/api/v1/tasks/1";
    TaskDto taskDto = restTemplate.getForObject(url, TaskDto.class);

    assertNotNull(taskDto);
    assertEquals("Develop login feature", taskDto.getDescription());
  }

  @Test
  @Sql({"/schema.sql", "/data.sql"})
  public void testUpdateTask() {
    String url = "http://localhost:" + port + "/api/v1/tasks/1";

    TaskRequestDto taskRequestDto = new TaskRequestDto();
    taskRequestDto.setDescription("Updated Task");
    taskRequestDto.setStatus(TaskStatus.IN_PROGRESS);
    taskRequestDto.setPriority(PriorityStatus.MEDIUM);
    taskRequestDto.setDueDate(LocalDate.now().plusDays(5));
    taskRequestDto.setProjectId(1);

    restTemplate.put(url, taskRequestDto, DetailedTaskDto.class);

    DetailedTaskDto response = restTemplate.getForObject(url, DetailedTaskDto.class);

    assertNotNull(response);
    assertEquals("Updated Task", response.getDescription());
  }

  @Test
  @Sql({"/schema.sql", "/data.sql"})
  public void testDeleteTask() {
    String url = "http://localhost:" + port + "/api/v1/tasks/1";
    restTemplate.delete(url);

    TaskDto taskDto = restTemplate.getForObject(url, TaskDto.class);
    assertNotNull(taskDto);
    assertEquals(null, taskDto.getDescription());
  }

  @Test
  @Sql({"/schema.sql", "/data.sql"})
  public void testAssignUserToTask() {
    String url = "http://localhost:" + port + "/api/v1/tasks/1/users/2";
    restTemplate.patchForObject(url, null, Void.class);

    String taskUrl = "http://localhost:" + port + "/api/v1/tasks/1";
    DetailedTaskDto taskDto = restTemplate.getForObject(taskUrl, DetailedTaskDto.class);

    assertNotNull(taskDto);
    assertEquals(1, taskDto.getUsers().size());
  }

  @Test
  @Sql({"/schema.sql", "/data.sql"})
  public void testRemoveUserFromTask() {
    String url = "http://localhost:" + port + "/api/v1/tasks/1/users/2";
    restTemplate.delete(url);

    String taskUrl = "http://localhost:" + port + "/api/v1/tasks/1";
    DetailedTaskDto taskDto = restTemplate.getForObject(taskUrl, DetailedTaskDto.class);

    assertNotNull(taskDto);
    assertEquals(1, taskDto.getUsers().size());
  }

  @Test
  @Sql({"/schema.sql", "/data.sql"})
  public void testGetByTaskIdWhenTaskDoesNotExist() {
    String url = "http://localhost:" + port + "/api/v1/tasks/100";
    TaskDto taskDto = restTemplate.getForObject(url, TaskDto.class);

    assertNotNull(taskDto);
    assertEquals(null, taskDto.getDescription());
  }

  @Test
  @Sql({"/schema.sql", "/data.sql"})
  public void testAssignUserToTaskWhenUserDoesNotExist() {
    String url = "http://localhost:" + port + "/api/v1/tasks/1/users/100";
    restTemplate.patchForObject(url, null, Void.class);

    String taskUrl = "http://localhost:" + port + "/api/v1/tasks/1";
    DetailedTaskDto taskDto = restTemplate.getForObject(taskUrl, DetailedTaskDto.class);

    assertNotNull(taskDto);
    assertEquals(1, taskDto.getUsers().size());
  }

  @Test
  @Sql({"/schema.sql", "/data.sql"})
  public void testRemoveUserFromTaskWhenUserDoesNotExist() {
    String url = "http://localhost:" + port + "/api/v1/tasks/1/users/100";
    restTemplate.delete(url);

    String taskUrl = "http://localhost:" + port + "/api/v1/tasks/1";
    DetailedTaskDto taskDto = restTemplate.getForObject(taskUrl, DetailedTaskDto.class);

    assertNotNull(taskDto);
    assertEquals(1, taskDto.getUsers().size());
  }
}
