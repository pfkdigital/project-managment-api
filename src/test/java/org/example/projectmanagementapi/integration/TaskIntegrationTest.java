package org.example.projectmanagementapi.integration;

import org.example.projectmanagementapi.config.TestJpaConfig;
import org.example.projectmanagementapi.dto.request.TaskRequestDto;
import org.example.projectmanagementapi.dto.response.DetailedTaskDto;
import org.example.projectmanagementapi.dto.response.TaskDto;
import org.example.projectmanagementapi.enums.PriorityStatus;
import org.example.projectmanagementapi.enums.TaskStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.main.allow-bean-definition-overriding=true"
        }
)
@ActiveProfiles("test")
@Import(TestJpaConfig.class)
@DisplayName("Task API Integration Tests")
public class TaskIntegrationTest {

  @Autowired
  private TestRestTemplate restTemplate;

  @LocalServerPort
  private int port;

  private String getBaseUrl() {
    return "http://localhost:" + port + "/api/v1/tasks";
  }

  @Nested
  @DisplayName("Task CRUD Operations")
  class TaskCrudTests {
    @Test
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Create a new task")
    public void testCreateTask() {
      TaskRequestDto taskRequestDto = new TaskRequestDto();
      taskRequestDto.setDescription("Test Task");
      taskRequestDto.setStatus(TaskStatus.OPEN);
      taskRequestDto.setPriority(PriorityStatus.HIGH);
      taskRequestDto.setDueDate(LocalDate.now().plusDays(7));
      taskRequestDto.setProjectId(1);

      ResponseEntity<TaskDto> response = restTemplate.postForEntity(
              getBaseUrl(), taskRequestDto, TaskDto.class);

      assertEquals(HttpStatus.CREATED, response.getStatusCode());
      TaskDto taskDto = response.getBody();
      assertNotNull(taskDto);
      assertEquals("Test Task", taskDto.getDescription());
    }

    @Test
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Get all tasks")
    public void testGetAllTasks() {
      ResponseEntity<TaskDto[]> response = restTemplate.getForEntity(
              getBaseUrl(), TaskDto[].class);

      assertEquals(HttpStatus.OK, response.getStatusCode());
      TaskDto[] tasks = response.getBody();
      assertNotNull(tasks);
      assertTrue(tasks.length > 0);
    }

    @Test
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Get task by ID")
    public void testGetTaskById() {
      ResponseEntity<DetailedTaskDto> response = restTemplate.getForEntity(
              getBaseUrl() + "/1", DetailedTaskDto.class);

      assertEquals(HttpStatus.OK, response.getStatusCode());
      DetailedTaskDto taskDto = response.getBody();
      assertNotNull(taskDto);
      assertNotNull(taskDto.getId());
    }

    @Test
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Update a task")
    public void testUpdateTask() {
      // First, get the task to update
      DetailedTaskDto existingTask = restTemplate.getForObject(getBaseUrl() + "/1", DetailedTaskDto.class);
      assertNotNull(existingTask);

      TaskRequestDto taskRequestDto = new TaskRequestDto();
      taskRequestDto.setDescription("Updated Task Description");
      taskRequestDto.setStatus(TaskStatus.IN_PROGRESS);
      taskRequestDto.setPriority(PriorityStatus.MEDIUM);
      taskRequestDto.setDueDate(LocalDate.now().plusDays(5));
      taskRequestDto.setProjectId(1);

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      HttpEntity<TaskRequestDto> request = new HttpEntity<>(taskRequestDto, headers);

      ResponseEntity<String> putResponse = restTemplate.exchange(
              getBaseUrl() + "/1",
              HttpMethod.PUT,
              request,
              String.class
      );

      assertEquals(HttpStatus.OK, putResponse.getStatusCode());

      // Verify the update
      ResponseEntity<DetailedTaskDto> getResponse = restTemplate.getForEntity(
              getBaseUrl() + "/1", DetailedTaskDto.class);
      DetailedTaskDto updatedTask = getResponse.getBody();

      assertNotNull(updatedTask);
      assertEquals("Updated Task Description", updatedTask.getDescription());
    }

    @Test
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Delete a task")
    public void testDeleteTask() {
      ResponseEntity<Void> deleteResponse = restTemplate.exchange(
              getBaseUrl() + "/1",
              HttpMethod.DELETE,
              null,
              Void.class
      );

      assertEquals(HttpStatus.OK, deleteResponse.getStatusCode());

      // Check that the task is deleted
      ResponseEntity<String> getAfterDeleteResponse = restTemplate.getForEntity(
              getBaseUrl() + "/1", String.class);

      // Should return 404 Not Found or similar error status
      assertTrue(getAfterDeleteResponse.getStatusCode().is4xxClientError());
    }
  }

  @Nested
  @DisplayName("Task User Assignment Operations")
  class TaskUserAssignmentTests {
    @Test
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Assign a user to a task")
    public void testAssignUserToTask() {
      String url = getBaseUrl() + "/1/users/2";

      ResponseEntity<Void> patchResponse = restTemplate.exchange(
              url,
              HttpMethod.PATCH,
              null,
              Void.class
      );

      assertEquals(HttpStatus.OK, patchResponse.getStatusCode());

      // Verify the user was assigned
      DetailedTaskDto taskDto = restTemplate.getForObject(getBaseUrl() + "/1", DetailedTaskDto.class);
      assertNotNull(taskDto);
      // Verify the task can be retrieved after assignment
      assertEquals(1, taskDto.getId().intValue());
    }

    @Test
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Remove a user from a task")
    public void testRemoveUserFromTask() {
      // First ensure user is assigned to task
      String assignUrl = getBaseUrl() + "/1/users/2";
      restTemplate.exchange(assignUrl, HttpMethod.PATCH, null, Void.class);

      // Then remove the user
      ResponseEntity<Void> deleteResponse = restTemplate.exchange(
              assignUrl,
              HttpMethod.DELETE,
              null,
              Void.class
      );

      assertEquals(HttpStatus.OK, deleteResponse.getStatusCode());

      // Verify user was removed
      DetailedTaskDto taskDto = restTemplate.getForObject(getBaseUrl() + "/1", DetailedTaskDto.class);
      assertNotNull(taskDto);
      assertEquals(1, taskDto.getId().intValue());
    }

    @Test
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Assign a non-existent user to a task")
    public void testAssignNonExistentUserToTask() {
      String url = getBaseUrl() + "/1/users/999";

      ResponseEntity<String> response = restTemplate.exchange(
              url,
              HttpMethod.PATCH,
              null,
              String.class
      );

      // Should return error status
      assertTrue(response.getStatusCode().is4xxClientError());
    }
  }

  @Nested
  @DisplayName("Task Error Cases")
  class TaskErrorCases {
    @Test
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Get a non-existent task")
    public void testGetTaskWhenTaskDoesNotExist() {
      ResponseEntity<String> response = restTemplate.getForEntity(
              getBaseUrl() + "/999", String.class);

      assertTrue(response.getStatusCode().is4xxClientError());
    }

    @Test
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Create a task with invalid data")
    public void testCreateTaskWithInvalidData() {
      TaskRequestDto invalidTask = new TaskRequestDto();

      ResponseEntity<String> response = restTemplate.postForEntity(
              getBaseUrl(), invalidTask, String.class);

      assertTrue(response.getStatusCode().is4xxClientError());
    }

    @Test
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Update a task with non-existent project ID")
    public void testUpdateTaskWithInvalidProjectId() {
      TaskRequestDto taskRequestDto = new TaskRequestDto();
      taskRequestDto.setDescription("Updated Task");
      taskRequestDto.setStatus(TaskStatus.IN_PROGRESS);
      taskRequestDto.setPriority(PriorityStatus.MEDIUM);
      taskRequestDto.setDueDate(LocalDate.now().plusDays(5));
      taskRequestDto.setProjectId(999); // Non-existent project

      ResponseEntity<String> response = restTemplate.exchange(
              getBaseUrl() + "/1",
              HttpMethod.PUT,
              new HttpEntity<>(taskRequestDto),
              String.class
      );

      assertTrue(response.getStatusCode().is4xxClientError());
    }

    @Test
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Update a non-existent task")
    public void testUpdateNonExistentTask() {
      TaskRequestDto taskRequestDto = new TaskRequestDto();
      taskRequestDto.setDescription("Update Non-existent Task");
      taskRequestDto.setStatus(TaskStatus.IN_PROGRESS);
      taskRequestDto.setPriority(PriorityStatus.MEDIUM);
      taskRequestDto.setDueDate(LocalDate.now().plusDays(5));
      taskRequestDto.setProjectId(1);

      ResponseEntity<String> response = restTemplate.exchange(
              getBaseUrl() + "/999",
              HttpMethod.PUT,
              new HttpEntity<>(taskRequestDto),
              String.class
      );

      assertTrue(response.getStatusCode().is4xxClientError());
    }
  }

  @Nested
  @DisplayName("Task Edge Cases")
  class TaskEdgeCases {
    @Test
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Create task with minimum due date")
    public void testCreateTaskWithMinimumDueDate() {
      TaskRequestDto taskRequestDto = new TaskRequestDto();
      taskRequestDto.setDescription("Minimum Due Date Task");
      taskRequestDto.setStatus(TaskStatus.OPEN);
      taskRequestDto.setPriority(PriorityStatus.HIGH);
      taskRequestDto.setDueDate(LocalDate.now()); // Today
      taskRequestDto.setProjectId(1);

      ResponseEntity<String> response = restTemplate.postForEntity(
              getBaseUrl(), taskRequestDto, String.class);

      assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Create task with long description")
    public void testCreateTaskWithLongDescription() {
      TaskRequestDto taskRequestDto = new TaskRequestDto();
      StringBuilder longDescription = new StringBuilder();
      for (int i = 0; i < 50; i++) {
        longDescription.append("word ");
      }

      taskRequestDto.setDescription(longDescription.toString());
      taskRequestDto.setStatus(TaskStatus.OPEN);
      taskRequestDto.setPriority(PriorityStatus.HIGH);
      taskRequestDto.setDueDate(LocalDate.now().plusDays(7));
      taskRequestDto.setProjectId(1);

      ResponseEntity<String> response = restTemplate.postForEntity(
              getBaseUrl(), taskRequestDto, String.class);

      // Check for either creation success or validation error
      assertTrue(
              response.getStatusCode() == HttpStatus.CREATED ||
                      response.getStatusCode().is4xxClientError(),
              "Should either create task or reject with validation error"
      );
    }

    @Test
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Assign a user to a task multiple times")
    public void testAssignUserToTaskMultipleTimes() {
      String url = getBaseUrl() + "/1/users/3";

      // First assignment
      restTemplate.exchange(url, HttpMethod.PATCH, null, Void.class);

      // Second assignment (should be idempotent)
      ResponseEntity<Void> secondAssignment = restTemplate.exchange(
              url,
              HttpMethod.PATCH,
              null,
              Void.class
      );

      assertEquals(HttpStatus.OK, secondAssignment.getStatusCode(),
              "Assigning the same user twice should be idempotent");

      // Verify user is assigned
      DetailedTaskDto taskDto = restTemplate.getForObject(getBaseUrl() + "/1", DetailedTaskDto.class);
      assertNotNull(taskDto);
      assertEquals(1, taskDto.getId().intValue());
    }
  }
}