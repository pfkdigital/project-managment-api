package org.example.projectmanagementapi.integration;

import org.example.projectmanagementapi.config.TestJpaConfig;
import org.example.projectmanagementapi.dto.request.IssueRequestDto;
import org.example.projectmanagementapi.enums.IssueStatus;
import org.example.projectmanagementapi.enums.PriorityStatus;
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
        properties = {"spring.main.allow-bean-definition-overriding=true"})
@ActiveProfiles("test")
@Import(TestJpaConfig.class)
@DisplayName("Issue API Integration Tests")
public class IssuesIntegrationTest {

  @Autowired
  private TestRestTemplate restTemplate;

  @LocalServerPort
  private int port;

  private String getBaseUrl() {
    return "http://localhost:" + port + "/api/v1/issues";
  }

  @Nested
  @DisplayName("Issue CRUD Operations")
  class IssueCrudTests {
    @Test
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Create a new issue")
    public void testCreateIssue() {
      IssueRequestDto issueRequestDto = new IssueRequestDto();
      issueRequestDto.setTitle("Test Issue");
      issueRequestDto.setDescription("Test Description");
      issueRequestDto.setStatus(IssueStatus.OPEN);
      issueRequestDto.setPriorityStatus(PriorityStatus.HIGH);
      issueRequestDto.setProjectId(1);
      issueRequestDto.setReportedById(1);
      issueRequestDto.setAssignedToId(2);

      ResponseEntity<String> response = restTemplate.postForEntity(
              getBaseUrl(), issueRequestDto, String.class);

      assertEquals(HttpStatus.CREATED, response.getStatusCode());
      assertTrue(response.getBody().contains("Test Issue"));
    }

    @Test
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Get all issues")
    public void testGetAllIssues() {
      ResponseEntity<String> response = restTemplate.getForEntity(
              getBaseUrl(), String.class);

      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertNotNull(response.getBody());

      // Check for expected issues from data.sql
      assertTrue(response.getBody().contains("Bug Fix A"));
      assertTrue(response.getBody().contains("UI Enhancement"));
    }

    @Test
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Get issue by ID")
    public void testGetIssueById() {
      ResponseEntity<String> response = restTemplate.getForEntity(
              getBaseUrl() + "/1", String.class);

      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertNotNull(response.getBody());
      assertTrue(response.getBody().contains("Bug Fix A"));
    }

    @Test
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Update an issue")
    public void testUpdateIssue() {
      IssueRequestDto issueRequestDto = new IssueRequestDto();
      issueRequestDto.setTitle("Updated Issue");
      issueRequestDto.setDescription("Updated Description");
      issueRequestDto.setStatus(IssueStatus.IN_PROGRESS);
      issueRequestDto.setPriorityStatus(PriorityStatus.MEDIUM);
      issueRequestDto.setProjectId(1);
      issueRequestDto.setReportedById(1);
      issueRequestDto.setAssignedToId(2);

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      HttpEntity<IssueRequestDto> request = new HttpEntity<>(issueRequestDto, headers);

      ResponseEntity<String> putResponse = restTemplate.exchange(
              getBaseUrl() + "/1",
              HttpMethod.PUT,
              request,
              String.class
      );

      assertEquals(HttpStatus.OK, putResponse.getStatusCode());

      // Verify the update
      ResponseEntity<String> getResponse = restTemplate.getForEntity(
              getBaseUrl() + "/1", String.class);

      assertTrue(getResponse.getBody().contains("Updated Issue"));
    }

    @Test
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Delete an issue")
    public void testDeleteIssue() {
      ResponseEntity<Void> deleteResponse = restTemplate.exchange(
              getBaseUrl() + "/1",
              HttpMethod.DELETE,
              null,
              Void.class
      );

      assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());

      // Verify issue is deleted
      ResponseEntity<String> getResponse = restTemplate.getForEntity(
              getBaseUrl() + "/1", String.class);

      assertTrue(getResponse.getStatusCode().is4xxClientError());
    }
  }

  @Nested
  @DisplayName("Issue Error Scenarios")
  class IssueErrorTests {
    @Test
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Get non-existent issue")
    public void testGetNonExistentIssue() {
      ResponseEntity<String> response = restTemplate.getForEntity(
              getBaseUrl() + "/999", String.class);

      assertTrue(response.getStatusCode().is4xxClientError());
      assertTrue(response.getBody().contains("not found"));
    }

    @Test
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Create issue with invalid data")
    public void testCreateIssueWithInvalidData() {
      IssueRequestDto invalidIssue = new IssueRequestDto();
      // Missing required fields

      ResponseEntity<String> response = restTemplate.postForEntity(
              getBaseUrl(), invalidIssue, String.class);

      assertTrue(response.getStatusCode().is4xxClientError());
    }

    @Test
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Update issue with non-existent project ID")
    public void testUpdateIssueWithInvalidProjectId() {
      IssueRequestDto issueRequestDto = new IssueRequestDto();
      issueRequestDto.setTitle("Updated Issue");
      issueRequestDto.setDescription("Updated Description");
      issueRequestDto.setStatus(IssueStatus.IN_PROGRESS);
      issueRequestDto.setPriorityStatus(PriorityStatus.MEDIUM);
      issueRequestDto.setProjectId(999); // Non-existent project
      issueRequestDto.setReportedById(1);
      issueRequestDto.setAssignedToId(2);

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      HttpEntity<IssueRequestDto> request = new HttpEntity<>(issueRequestDto, headers);

      ResponseEntity<String> response = restTemplate.exchange(
              getBaseUrl() + "/1",
              HttpMethod.PUT,
              request,
              String.class
      );
      System.out.println(response);
      assertTrue(response.getStatusCode().is4xxClientError());
    }

    @Test
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Update issue with non-existent user ID")
    public void testUpdateIssueWithInvalidUserId() {
      IssueRequestDto issueRequestDto = new IssueRequestDto();
      issueRequestDto.setTitle("Updated Issue");
      issueRequestDto.setDescription("Updated Description");
      issueRequestDto.setStatus(IssueStatus.IN_PROGRESS);
      issueRequestDto.setPriorityStatus(PriorityStatus.MEDIUM);
      issueRequestDto.setProjectId(1);
      issueRequestDto.setReportedById(1);
      issueRequestDto.setAssignedToId(999); // Non-existent user

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      HttpEntity<IssueRequestDto> request = new HttpEntity<>(issueRequestDto, headers);

      ResponseEntity<String> response = restTemplate.exchange(
              getBaseUrl() + "/1",
              HttpMethod.PUT,
              request,
              String.class
      );

      assertTrue(response.getStatusCode().is4xxClientError());
    }
  }

  @Nested
  @DisplayName("Issue Edge Cases")
  class IssueEdgeCases {
    @Test
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Update issue with same data")
    public void testUpdateIssueWithSameData() {
      // First, get current issue data
      ResponseEntity<String> getResponse = restTemplate.getForEntity(
              getBaseUrl() + "/1", String.class);

      String originalBody = getResponse.getBody();

      // Create update DTO with the same data
      IssueRequestDto issueRequestDto = new IssueRequestDto();
      issueRequestDto.setTitle("Bug Fix A");
      issueRequestDto.setDescription("Fix a critical bug in system");
      issueRequestDto.setStatus(IssueStatus.OPEN);
      issueRequestDto.setPriorityStatus(PriorityStatus.HIGH);
      issueRequestDto.setProjectId(1);
      issueRequestDto.setReportedById(1);
      issueRequestDto.setAssignedToId(2);

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      HttpEntity<IssueRequestDto> request = new HttpEntity<>(issueRequestDto, headers);

      ResponseEntity<String> putResponse = restTemplate.exchange(
              getBaseUrl() + "/1",
              HttpMethod.PUT,
              request,
              String.class
      );

      assertEquals(HttpStatus.OK, putResponse.getStatusCode());
    }
  }
}