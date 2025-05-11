package org.example.projectmanagementapi.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.projectmanagementapi.config.TestJpaConfig;
import org.example.projectmanagementapi.dto.request.CommentRequestDto;
import org.example.projectmanagementapi.dto.request.CommentUpdateRequest;
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
        properties = {
                "spring.main.allow-bean-definition-overriding=true",
        })
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestJpaConfig.class)
@DisplayName("Comment API Integration Tests")
public class CommentsIntegrationTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  private String getBaseUrl() {
    return "/api/v1/comments";
  }

  @Nested
  @DisplayName("Comment CRUD Operations")
  class CommentCrudTests {
    @Test
    @WithMockUser(roles = "USER")
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Create a new task comment")
    public void testCreateTaskComment() throws Exception {
      CommentRequestDto commentRequestDto = new CommentRequestDto();
      commentRequestDto.setContent("New test comment for task");
      commentRequestDto.setTaskId(1);
      commentRequestDto.setAuthorId(2);

      mockMvc.perform(post(getBaseUrl())
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(objectMapper.writeValueAsString(commentRequestDto)))
              .andExpect(status().isCreated())
              .andExpect(content().string(containsString("New test comment for task")));
    }

    @Test
    @WithMockUser(roles = "USER")
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Create a new issue comment")
    public void testCreateIssueComment() throws Exception {
      CommentRequestDto commentRequestDto = new CommentRequestDto();
      commentRequestDto.setContent("New test comment for issue");
      commentRequestDto.setIssueId(1);
      commentRequestDto.setAuthorId(2);

      mockMvc.perform(post(getBaseUrl())
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(objectMapper.writeValueAsString(commentRequestDto)))
              .andExpect(status().isCreated())
              .andExpect(content().string(containsString("New test comment for issue")));
    }

    @Test
    @WithMockUser(roles = "USER")
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Get all comments")
    public void testGetAllComments() throws Exception {
      mockMvc.perform(get(getBaseUrl()))
              .andExpect(status().isOk())
              .andExpect(content().string(containsString("This task is almost done")))
              .andExpect(content().string(containsString("We need more details")));
    }

    @Test
    @WithMockUser(roles = "USER")
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Get comment by ID")
    public void testGetCommentById() throws Exception {
      mockMvc.perform(get(getBaseUrl() + "/1"))
              .andExpect(status().isOk())
              .andExpect(content().string(containsString("This task is almost done")));
    }

    @Test
    @WithMockUser(roles = "USER")
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Update a comment")
    public void testUpdateComment() throws Exception {
      CommentUpdateRequest commentUpdateRequest = new CommentUpdateRequest();
      commentUpdateRequest.setContent("Updated comment content");
      commentUpdateRequest.setAuthorId(2);

      mockMvc.perform(put(getBaseUrl() + "/1")
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(objectMapper.writeValueAsString(commentUpdateRequest)))
              .andExpect(status().isOk())
              .andExpect(content().string(containsString("Updated comment content")));
    }

    @Test
    @WithMockUser(roles = "USER")
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Delete a comment")
    public void testDeleteComment() throws Exception {
      mockMvc.perform(delete(getBaseUrl() + "/1"))
              .andExpect(status().isNoContent());

      // Verify comment is deleted
      mockMvc.perform(get(getBaseUrl() + "/1"))
              .andExpect(status().is4xxClientError());
    }
  }

  @Nested
  @DisplayName("Comment Error Scenarios")
  class CommentErrorTests {
    @Test
    @WithMockUser(roles = "USER")
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Get non-existent comment")
    public void testGetNonExistentComment() throws Exception {
      mockMvc.perform(get(getBaseUrl() + "/999"))
              .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(roles = "USER")
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Create comment with invalid data")
    public void testCreateCommentWithInvalidData() throws Exception {
      CommentRequestDto invalidComment = new CommentRequestDto();
      // Missing required fields

      mockMvc.perform(post(getBaseUrl())
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(objectMapper.writeValueAsString(invalidComment)))
              .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(roles = "USER")
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Create comment with non-existent task ID")
    public void testCreateCommentWithInvalidTaskId() throws Exception {
      CommentRequestDto commentRequestDto = new CommentRequestDto();
      commentRequestDto.setContent("Test comment");
      commentRequestDto.setIssueId(null);
      commentRequestDto.setTaskId(999); // Non-existent task
      commentRequestDto.setAuthorId(2);

      mockMvc.perform(post(getBaseUrl())
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(objectMapper.writeValueAsString(commentRequestDto)))
              .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(roles = "USER")
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Update comment with non-existent author ID")
    public void testUpdateCommentWithInvalidAuthorId() throws Exception {
      CommentUpdateRequest commentUpdateRequest = new CommentUpdateRequest();
      commentUpdateRequest.setContent("Updated content");
      commentUpdateRequest.setAuthorId(999); // Non-existent user

      mockMvc.perform(put(getBaseUrl() + "/1")
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(objectMapper.writeValueAsString(commentUpdateRequest)))
              .andExpect(status().is4xxClientError());
    }
  }

  @Nested
  @DisplayName("Comment Edge Cases")
  class CommentEdgeCases {
    @Test
    @WithMockUser(roles = "USER")
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Create comment with long content")
    public void testCreateCommentWithLongContent() throws Exception {
      CommentRequestDto commentRequestDto = new CommentRequestDto();

      // Generate a long comment (255+ chars)
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < 10; i++) {
        sb.append("This is a very long comment that should test the limits of the content field. ");
      }

      commentRequestDto.setContent(sb.toString());
      commentRequestDto.setTaskId(1);
      commentRequestDto.setAuthorId(2);

      mockMvc.perform(post(getBaseUrl())
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(objectMapper.writeValueAsString(commentRequestDto)));
    }

    @Test
    @WithMockUser(roles = "USER")
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Update comment with same data")
    public void testUpdateCommentWithSameData() throws Exception {
      CommentUpdateRequest commentUpdateRequest = new CommentUpdateRequest();
      commentUpdateRequest.setContent("This task is almost done.");
      commentUpdateRequest.setAuthorId(2);

      mockMvc.perform(put(getBaseUrl() + "/1")
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(objectMapper.writeValueAsString(commentUpdateRequest)))
              .andExpect(status().isOk());
    }
  }
}