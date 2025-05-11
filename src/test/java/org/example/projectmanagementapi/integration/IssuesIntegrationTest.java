package org.example.projectmanagementapi.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.projectmanagementapi.config.TestJpaConfig;
import org.example.projectmanagementapi.dto.request.IssueRequestDto;
import org.example.projectmanagementapi.enums.IssueStatus;
import org.example.projectmanagementapi.enums.PriorityStatus;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest(
        properties = {"spring.main.allow-bean-definition-overriding=true"})
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestJpaConfig.class)
@DisplayName("Issue API Integration Tests")
public class IssuesIntegrationTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  private String getBaseUrl() {
    return "/api/v1/issues";
  }

  @Nested
  @DisplayName("Issue CRUD Operations")
  class IssueCrudTests {
    @Test
    @WithMockUser(roles = "USER")
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Create a new issue")
    public void testCreateIssue() throws Exception {
      IssueRequestDto issueRequestDto = new IssueRequestDto();
      issueRequestDto.setTitle("Test Issue");
      issueRequestDto.setDescription("Test Description");
      issueRequestDto.setStatus(IssueStatus.OPEN);
      issueRequestDto.setPriorityStatus(PriorityStatus.HIGH);
      issueRequestDto.setProjectId(1);
      issueRequestDto.setReportedById(1);
      issueRequestDto.setAssignedToId(2);

      mockMvc.perform(post(getBaseUrl())
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(objectMapper.writeValueAsString(issueRequestDto)))
              .andExpect(status().isCreated())
              .andExpect(content().string(containsString("Test Issue")));
    }

    @Test
    @WithMockUser(roles = "USER")
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Get all issues")
    public void testGetAllIssues() throws Exception {
      mockMvc.perform(get(getBaseUrl()))
              .andExpect(status().isOk())
              .andExpect(content().string(containsString("Bug Fix A")))
              .andExpect(content().string(containsString("UI Enhancement")));
    }

    @Test
    @WithMockUser(roles = "USER")
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Get issue by ID")
    public void testGetIssueById() throws Exception {
      mockMvc.perform(get(getBaseUrl() + "/1"))
              .andExpect(status().isOk())
              .andExpect(content().string(containsString("Bug Fix A")));
    }

    @Test
    @WithMockUser(roles = "USER")
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Update an issue")
    public void testUpdateIssue() throws Exception {
      IssueRequestDto issueRequestDto = new IssueRequestDto();
      issueRequestDto.setTitle("Updated Issue");
      issueRequestDto.setDescription("Updated Description");
      issueRequestDto.setStatus(IssueStatus.IN_PROGRESS);
      issueRequestDto.setPriorityStatus(PriorityStatus.MEDIUM);
      issueRequestDto.setProjectId(1);
      issueRequestDto.setReportedById(1);
      issueRequestDto.setAssignedToId(2);

      mockMvc.perform(put(getBaseUrl() + "/1")
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(objectMapper.writeValueAsString(issueRequestDto)))
              .andExpect(status().isOk());

      // Verify the update
      mockMvc.perform(get(getBaseUrl() + "/1"))
              .andExpect(status().isOk())
              .andExpect(content().string(containsString("Updated Issue")));
    }

    @Test
    @WithMockUser(roles = "USER")
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Delete an issue")
    public void testDeleteIssue() throws Exception {
      mockMvc.perform(delete(getBaseUrl() + "/1"))
              .andExpect(status().isNoContent());

      // Verify issue is deleted
      mockMvc.perform(get(getBaseUrl() + "/1"))
              .andExpect(status().is4xxClientError());
    }
  }

  @Nested
  @DisplayName("Issue Error Scenarios")
  class IssueErrorTests {
    @Test
    @WithMockUser(roles = "USER")
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Get non-existent issue")
    public void testGetNonExistentIssue() throws Exception {
      mockMvc.perform(get(getBaseUrl() + "/999"))
              .andExpect(status().is4xxClientError())
              .andExpect(content().string(containsString("not found")));
    }

    @Test
    @WithMockUser(roles = "USER")
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Create issue with invalid data")
    public void testCreateIssueWithInvalidData() throws Exception {
      IssueRequestDto invalidIssue = new IssueRequestDto();
      // Missing required fields

      mockMvc.perform(post(getBaseUrl())
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(objectMapper.writeValueAsString(invalidIssue)))
              .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(roles = "USER")
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Update issue with non-existent project ID")
    public void testUpdateIssueWithInvalidProjectId() throws Exception {
      IssueRequestDto issueRequestDto = new IssueRequestDto();
      issueRequestDto.setTitle("Updated Issue");
      issueRequestDto.setDescription("Updated Description");
      issueRequestDto.setStatus(IssueStatus.IN_PROGRESS);
      issueRequestDto.setPriorityStatus(PriorityStatus.MEDIUM);
      issueRequestDto.setProjectId(999); // Non-existent project
      issueRequestDto.setReportedById(1);
      issueRequestDto.setAssignedToId(2);

      mockMvc.perform(put(getBaseUrl() + "/1")
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(objectMapper.writeValueAsString(issueRequestDto)))
              .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(roles = "USER")
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Update issue with non-existent user ID")
    public void testUpdateIssueWithInvalidUserId() throws Exception {
      IssueRequestDto issueRequestDto = new IssueRequestDto();
      issueRequestDto.setTitle("Updated Issue");
      issueRequestDto.setDescription("Updated Description");
      issueRequestDto.setStatus(IssueStatus.IN_PROGRESS);
      issueRequestDto.setPriorityStatus(PriorityStatus.MEDIUM);
      issueRequestDto.setProjectId(1);
      issueRequestDto.setReportedById(1);
      issueRequestDto.setAssignedToId(999); // Non-existent user

      mockMvc.perform(put(getBaseUrl() + "/1")
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(objectMapper.writeValueAsString(issueRequestDto)))
              .andExpect(status().is4xxClientError());
    }
  }

  @Nested
  @DisplayName("Issue Edge Cases")
  class IssueEdgeCases {
    @Test
    @WithMockUser(roles = "USER")
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Update issue with same data")
    public void testUpdateIssueWithSameData() throws Exception {
      // Create update DTO with the same data
      IssueRequestDto issueRequestDto = new IssueRequestDto();
      issueRequestDto.setTitle("Bug Fix A");
      issueRequestDto.setDescription("Fix a critical bug in system");
      issueRequestDto.setStatus(IssueStatus.OPEN);
      issueRequestDto.setPriorityStatus(PriorityStatus.HIGH);
      issueRequestDto.setProjectId(1);
      issueRequestDto.setReportedById(1);
      issueRequestDto.setAssignedToId(2);

      mockMvc.perform(put(getBaseUrl() + "/1")
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(objectMapper.writeValueAsString(issueRequestDto)))
              .andExpect(status().isOk());
    }
  }
}