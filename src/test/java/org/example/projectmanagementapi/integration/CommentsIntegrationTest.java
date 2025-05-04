package org.example.projectmanagementapi.integration;

import org.example.projectmanagementapi.config.TestJpaConfig;
import org.example.projectmanagementapi.dto.request.CommentRequestDto;
import org.example.projectmanagementapi.dto.request.CommentUpdateRequest;
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

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {
      "spring.main.allow-bean-definition-overriding=true",
    })
@ActiveProfiles("test")
@Import(TestJpaConfig.class)
@DisplayName("Comment API Integration Tests")
public class CommentsIntegrationTest {

  @Autowired private TestRestTemplate restTemplate;

  @LocalServerPort private int port;

  private String getBaseUrl() {
    return "http://localhost:" + port + "/api/v1/comments";
  }

  @Nested
  @DisplayName("Comment CRUD Operations")
  class CommentCrudTests {
    @Test
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Create a new task comment")
    public void testCreateTaskComment() {
      CommentRequestDto commentRequestDto = new CommentRequestDto();
      commentRequestDto.setContent("New test comment for task");
      commentRequestDto.setTaskId(1);
      commentRequestDto.setAuthorId(2);

      ResponseEntity<String> response =
          restTemplate.postForEntity(getBaseUrl(), commentRequestDto, String.class);

      assertEquals(HttpStatus.CREATED, response.getStatusCode());
      assertNotNull(response.getBody());
      assertTrue(response.getBody().contains("New test comment for task"));
    }

    @Test
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Create a new issue comment")
    public void testCreateIssueComment() {
      CommentRequestDto commentRequestDto = new CommentRequestDto();
      commentRequestDto.setContent("New test comment for issue");
      commentRequestDto.setIssueId(1);
      commentRequestDto.setAuthorId(2);

      ResponseEntity<String> response =
          restTemplate.postForEntity(getBaseUrl(), commentRequestDto, String.class);

      assertEquals(HttpStatus.CREATED, response.getStatusCode());
      assertNotNull(response.getBody());
      assertTrue(response.getBody().contains("New test comment for issue"));
    }

    @Test
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Get all comments")
    public void testGetAllComments() {
      ResponseEntity<String> response = restTemplate.getForEntity(getBaseUrl(), String.class);

      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertNotNull(response.getBody());
      // Verify existing comments from data.sql are present
      assertTrue(response.getBody().contains("This task is almost done"));
      assertTrue(response.getBody().contains("We need more details"));
    }

    @Test
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Get comment by ID")
    public void testGetCommentById() {
      ResponseEntity<String> response =
          restTemplate.getForEntity(getBaseUrl() + "/1", String.class);

      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertNotNull(response.getBody());
      assertTrue(response.getBody().contains("This task is almost done"));
    }

    @Test
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Update a comment")
    public void testUpdateComment() {
      CommentUpdateRequest commentUpdateRequest = new CommentUpdateRequest();
      commentUpdateRequest.setContent("Updated comment content");
      commentUpdateRequest.setAuthorId(2);

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      HttpEntity<CommentUpdateRequest> request = new HttpEntity<>(commentUpdateRequest, headers);

      ResponseEntity<String> response =
          restTemplate.exchange(getBaseUrl() + "/1", HttpMethod.PUT, request, String.class);

      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertNotNull(response.getBody());
      assertTrue(response.getBody().contains("Updated comment content"));
    }

    @Test
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Delete a comment")
    public void testDeleteComment() {
      ResponseEntity<Void> deleteResponse =
          restTemplate.exchange(getBaseUrl() + "/1", HttpMethod.DELETE, null, Void.class);

      assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());

      // Verify comment is deleted
      ResponseEntity<String> getResponse =
          restTemplate.getForEntity(getBaseUrl() + "/1", String.class);
      assertTrue(getResponse.getStatusCode().is4xxClientError());
    }
  }

  @Nested
  @DisplayName("Comment Error Scenarios")
  class CommentErrorTests {
    @Test
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Get non-existent comment")
    public void testGetNonExistentComment() {
      ResponseEntity<String> response =
          restTemplate.getForEntity(getBaseUrl() + "/999", String.class);

      assertTrue(response.getStatusCode().is4xxClientError());
    }

    @Test
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Create comment with invalid data")
    public void testCreateCommentWithInvalidData() {
      CommentRequestDto invalidComment = new CommentRequestDto();
      // Missing required fields

      ResponseEntity<String> response =
          restTemplate.postForEntity(getBaseUrl(), invalidComment, String.class);

      assertTrue(response.getStatusCode().is4xxClientError());
    }

    @Test
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Create comment with non-existent task ID")
    public void testCreateCommentWithInvalidTaskId() {
      CommentRequestDto commentRequestDto = new CommentRequestDto();
      commentRequestDto.setContent("Test comment");
      commentRequestDto.setIssueId(null);
      commentRequestDto.setTaskId(999); // Non-existent task
      commentRequestDto.setAuthorId(2);

      ResponseEntity<String> response =
          restTemplate.postForEntity(getBaseUrl(), commentRequestDto, String.class);
      System.out.println(response);
      assertTrue(response.getStatusCode().is4xxClientError());
    }

    @Test
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Update comment with non-existent author ID")
    public void testUpdateCommentWithInvalidAuthorId() {
      CommentUpdateRequest commentUpdateRequest = new CommentUpdateRequest();
      commentUpdateRequest.setContent("Updated content");
      commentUpdateRequest.setAuthorId(999); // Non-existent user

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      HttpEntity<CommentUpdateRequest> request = new HttpEntity<>(commentUpdateRequest, headers);

      ResponseEntity<String> response =
          restTemplate.exchange(getBaseUrl() + "/1", HttpMethod.PUT, request, String.class);

      assertTrue(response.getStatusCode().is4xxClientError());
    }
  }

  @Nested
  @DisplayName("Comment Edge Cases")
  class CommentEdgeCases {
    @Test
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Create comment with long content")
    public void testCreateCommentWithLongContent() {
      CommentRequestDto commentRequestDto = new CommentRequestDto();

      // Generate a long comment (255+ chars)
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < 10; i++) {
        sb.append("This is a very long comment that should test the limits of the content field. ");
      }

      commentRequestDto.setContent(sb.toString());
      commentRequestDto.setTaskId(1);
      commentRequestDto.setAuthorId(2);

      ResponseEntity<String> response =
          restTemplate.postForEntity(getBaseUrl(), commentRequestDto, String.class);
      System.out.println(response);
      assertTrue(response.getStatusCode() == HttpStatus.BAD_REQUEST || response.getStatusCode().is4xxClientError(), "Should either create comment or reject with validation error");
    }

    @Test
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Update comment with same data")
    public void testUpdateCommentWithSameData() {
      // First get current comment data
      ResponseEntity<String> getResponse =
          restTemplate.getForEntity(getBaseUrl() + "/1", String.class);

      String originalBody = getResponse.getBody();

      // Create update request with same content
      CommentUpdateRequest commentUpdateRequest = new CommentUpdateRequest();
      commentUpdateRequest.setContent("This task is almost done.");
      commentUpdateRequest.setAuthorId(2);

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      HttpEntity<CommentUpdateRequest> request = new HttpEntity<>(commentUpdateRequest, headers);

      ResponseEntity<String> putResponse =
          restTemplate.exchange(getBaseUrl() + "/1", HttpMethod.PUT, request, String.class);

      assertEquals(HttpStatus.OK, putResponse.getStatusCode());
    }
  }
}
