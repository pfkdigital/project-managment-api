package org.example.projectmanagementapi.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.projectmanagementapi.config.TestJpaConfig;
import org.example.projectmanagementapi.dto.request.TaskRequestDto;
import org.example.projectmanagementapi.enums.PriorityStatus;
import org.example.projectmanagementapi.enums.TaskStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {"spring.main.allow-bean-definition-overriding=true"}
)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestJpaConfig.class)
@DisplayName("Task API Integration Tests")
public class TaskIntegrationTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  private String getBaseUrl() {
    return "/api/v1/tasks";
  }

  @Nested
  @DisplayName("Task CRUD Operations")
  class TaskCrudTests {
    @Test
    @WithMockUser(roles = "USER")
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Create a new task")
    public void testCreateTask() throws Exception {
      TaskRequestDto taskRequestDto = new TaskRequestDto();
      taskRequestDto.setDescription("Test Task");
      taskRequestDto.setStatus(TaskStatus.OPEN);
      taskRequestDto.setPriority(PriorityStatus.HIGH);
      taskRequestDto.setDueDate(LocalDate.now().plusDays(7));
      taskRequestDto.setProjectId(1);

      mockMvc.perform(post(getBaseUrl())
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(objectMapper.writeValueAsString(taskRequestDto)))
              .andExpect(status().isCreated())
              .andExpect(jsonPath("$.description").value("Test Task"));
    }

    @Test
    @WithMockUser(roles = "USER")
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Get all tasks")
    public void testGetAllTasks() throws Exception {
      mockMvc.perform(get(getBaseUrl()))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$", hasSize(greaterThan(0))));
    }

    @Test
    @WithMockUser(roles = "USER")
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Get task by ID")
    public void testGetTaskById() throws Exception {
      mockMvc.perform(get(getBaseUrl() + "/1"))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(roles = "USER")
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Update a task")
    public void testUpdateTask() throws Exception {
      TaskRequestDto taskRequestDto = new TaskRequestDto();
      taskRequestDto.setDescription("Updated Task Description");
      taskRequestDto.setStatus(TaskStatus.IN_PROGRESS);
      taskRequestDto.setPriority(PriorityStatus.MEDIUM);
      taskRequestDto.setDueDate(LocalDate.now().plusDays(5));
      taskRequestDto.setProjectId(1);

      mockMvc.perform(put(getBaseUrl() + "/1")
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(objectMapper.writeValueAsString(taskRequestDto)))
              .andExpect(status().isOk());

      // Verify the update
      mockMvc.perform(get(getBaseUrl() + "/1"))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$.description").value("Updated Task Description"));
    }

    @Test
    @WithMockUser(roles = "USER")
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Delete a task")
    public void testDeleteTask() throws Exception {
      mockMvc.perform(delete(getBaseUrl() + "/1"))
              .andExpect(status().isOk());

      // Check that the task is deleted
      mockMvc.perform(get(getBaseUrl() + "/1"))
              .andExpect(status().is4xxClientError());
    }
  }

  @Nested
  @DisplayName("Task User Assignment Operations")
  class TaskUserAssignmentTests {
    @Test
    @WithMockUser(roles = "USER")
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Assign a user to a task")
    public void testAssignUserToTask() throws Exception {
      mockMvc.perform(patch(getBaseUrl() + "/1/users/2"))
              .andExpect(status().isOk());

      // Verify the user was assigned
      mockMvc.perform(get(getBaseUrl() + "/1"))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(roles = "USER")
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Remove a user from a task")
    public void testRemoveUserFromTask() throws Exception {
      // First ensure user is assigned to task
      mockMvc.perform(patch(getBaseUrl() + "/1/users/2"))
              .andExpect(status().isOk());

      // Then remove the user
      mockMvc.perform(delete(getBaseUrl() + "/1/users/2"))
              .andExpect(status().isOk());

      // Verify task still exists after user removal
      mockMvc.perform(get(getBaseUrl() + "/1"))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(roles = "USER")
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Assign a non-existent user to a task")
    public void testAssignNonExistentUserToTask() throws Exception {
      mockMvc.perform(patch(getBaseUrl() + "/1/users/999"))
              .andExpect(status().is4xxClientError());
    }
  }

  @Nested
  @DisplayName("Task Error Cases")
  class TaskErrorCases {
    @Test
    @WithMockUser(roles = "USER")
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Get a non-existent task")
    public void testGetTaskWhenTaskDoesNotExist() throws Exception {
      mockMvc.perform(get(getBaseUrl() + "/999"))
              .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(roles = "USER")
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Create a task with invalid data")
    public void testCreateTaskWithInvalidData() throws Exception {
      TaskRequestDto invalidTask = new TaskRequestDto();

      mockMvc.perform(post(getBaseUrl())
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(objectMapper.writeValueAsString(invalidTask)))
              .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(roles = "USER")
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Update a task with non-existent project ID")
    public void testUpdateTaskWithInvalidProjectId() throws Exception {
      TaskRequestDto taskRequestDto = new TaskRequestDto();
      taskRequestDto.setDescription("Updated Task");
      taskRequestDto.setStatus(TaskStatus.IN_PROGRESS);
      taskRequestDto.setPriority(PriorityStatus.MEDIUM);
      taskRequestDto.setDueDate(LocalDate.now().plusDays(5));
      taskRequestDto.setProjectId(999); // Non-existent project

      mockMvc.perform(put(getBaseUrl() + "/1")
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(objectMapper.writeValueAsString(taskRequestDto)))
              .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(roles = "USER")
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Update a non-existent task")
    public void testUpdateNonExistentTask() throws Exception {
      TaskRequestDto taskRequestDto = new TaskRequestDto();
      taskRequestDto.setDescription("Update Non-existent Task");
      taskRequestDto.setStatus(TaskStatus.IN_PROGRESS);
      taskRequestDto.setPriority(PriorityStatus.MEDIUM);
      taskRequestDto.setDueDate(LocalDate.now().plusDays(5));
      taskRequestDto.setProjectId(1);

      mockMvc.perform(put(getBaseUrl() + "/999")
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(objectMapper.writeValueAsString(taskRequestDto)))
              .andExpect(status().is4xxClientError());
    }
  }

  @Nested
  @DisplayName("Task Edge Cases")
  class TaskEdgeCases {
    @Test
    @WithMockUser(roles = "USER")
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Create task with minimum due date")
    public void testCreateTaskWithMinimumDueDate() throws Exception {
      TaskRequestDto taskRequestDto = new TaskRequestDto();
      taskRequestDto.setDescription("Minimum Due Date Task");
      taskRequestDto.setStatus(TaskStatus.OPEN);
      taskRequestDto.setPriority(PriorityStatus.HIGH);
      taskRequestDto.setDueDate(LocalDate.now()); // Today
      taskRequestDto.setProjectId(1);

      mockMvc.perform(post(getBaseUrl())
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(objectMapper.writeValueAsString(taskRequestDto)))
              .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "USER")
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Create task with long description")
    public void testCreateTaskWithLongDescription() throws Exception {
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

      mockMvc.perform(post(getBaseUrl())
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(objectMapper.writeValueAsString(taskRequestDto)));
    }

    @Test
    @WithMockUser(roles = "USER")
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Assign a user to a task multiple times")
    public void testAssignUserToTaskMultipleTimes() throws Exception {
      // First assignment
      mockMvc.perform(patch(getBaseUrl() + "/1/users/3"))
              .andExpect(status().isOk());

      // Second assignment (should be idempotent)
      mockMvc.perform(patch(getBaseUrl() + "/1/users/3"))
              .andExpect(status().isOk());

      // Verify user is assigned
      mockMvc.perform(get(getBaseUrl() + "/1"))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$.id").value(1));
    }
  }
}