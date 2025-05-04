package org.example.projectmanagementapi.integration;

import org.example.projectmanagementapi.config.TestJpaConfig;
import org.example.projectmanagementapi.dto.request.ProjectRequestDto;
import org.example.projectmanagementapi.enums.ProjectStatus;
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
@DisplayName("Project API Integration Tests")
public class ProjectsIntegrationTest {

  @Autowired private TestRestTemplate restTemplate;

  @LocalServerPort private int port;

  private String getBaseUrl() {
    return "http://localhost:" + port + "/api/v1/projects";
  }

  @Nested
  @DisplayName("Project CRUD Operations")
  class ProjectCrudTests {
    @Test
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Create a new project")
    public void testCreateProject() {
      ProjectRequestDto projectRequestDto = new ProjectRequestDto();
      projectRequestDto.setName("Test Project");
      projectRequestDto.setDescription("Test Description");
      projectRequestDto.setStatus(ProjectStatus.ACTIVE);
      projectRequestDto.setDisplayImageUrl("http://example.com/image.png");
      projectRequestDto.setOwnerId(1);

      ResponseEntity<String> response =
          restTemplate.postForEntity(getBaseUrl(), projectRequestDto, String.class);

      assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Get all projects")
    public void testGetAllProjects() {
      ResponseEntity<String> response = restTemplate.getForEntity(getBaseUrl(), String.class);

      assertEquals(HttpStatus.OK, response.getStatusCode());
      String body = response.getBody();
      assertNotNull(body);
      assertTrue(body.contains("Project Alpha"));
    }

    @Test
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Get project by ID")
    public void testGetProjectById() {
      ResponseEntity<String> response =
          restTemplate.getForEntity(getBaseUrl() + "/1", String.class);

      assertEquals(HttpStatus.OK, response.getStatusCode());
      String body = response.getBody();
      assertNotNull(body);
      assertTrue(body.contains("Project Alpha"));
    }

    @Test
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Update a project")
    public void testUpdateProject() {
      ProjectRequestDto projectRequestDto = new ProjectRequestDto();
      projectRequestDto.setName("Updated Project Alpha");
      projectRequestDto.setDescription("Updated first test project");
      projectRequestDto.setStatus(ProjectStatus.ACTIVE);
      projectRequestDto.setDisplayImageUrl("http://example.com/updated_image.png");
      projectRequestDto.setOwnerId(1);

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      HttpEntity<ProjectRequestDto> request = new HttpEntity<>(projectRequestDto, headers);

      ResponseEntity<String> response =
          restTemplate.exchange(getBaseUrl() + "/1", HttpMethod.PUT, request, String.class);

      assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Delete a project")
    public void testDeleteProject() {
      ResponseEntity<String> response =
          restTemplate.exchange(getBaseUrl() + "/1", HttpMethod.DELETE, null, String.class);

      assertEquals(HttpStatus.OK, response.getStatusCode());

      ResponseEntity<String> getResponse =
          restTemplate.getForEntity(getBaseUrl() + "/1", String.class);
      System.out.println(getResponse);
      assertTrue(getResponse.getStatusCode().is4xxClientError());
    }
  }

  @Nested
  @DisplayName("Project Collaborator Operations")
  class ProjectCollaboratorTests {
    @Test
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Add collaborator to a project")
    public void testAddCollaborator() {
      ResponseEntity<String> response =
          restTemplate.exchange(
              getBaseUrl() + "/1/collaborators/4", HttpMethod.PATCH, null, String.class);

      assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Remove collaborator from a project")
    public void testRemoveCollaborator() {
      ResponseEntity<String> response =
          restTemplate.exchange(
              getBaseUrl() + "/1/collaborators/2", HttpMethod.DELETE, null, String.class);

      assertEquals(HttpStatus.OK, response.getStatusCode());
    }
  }

  @Nested
  @DisplayName("Project Error Cases")
  class ProjectErrorTests {
    @Test
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Get non-existent project")
    public void testGetNonExistentProject() {
      ResponseEntity<String> response =
          restTemplate.getForEntity(getBaseUrl() + "/999", String.class);

      assertTrue(response.getStatusCode().is4xxClientError());
    }

    @Test
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Create project with missing required fields")
    public void testCreateProjectWithMissingFields() {
      ProjectRequestDto projectRequestDto = new ProjectRequestDto();
      // Missing required fields

      ResponseEntity<String> response =
          restTemplate.postForEntity(getBaseUrl(), projectRequestDto, String.class);

      assertTrue(response.getStatusCode().is4xxClientError());
    }

    @Test
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Add non-existent user as collaborator")
    public void testAddNonExistentCollaborator() {
      ResponseEntity<String> response =
          restTemplate.exchange(
              getBaseUrl() + "/1/collaborators/999", HttpMethod.PATCH, null, String.class);

      assertTrue(response.getStatusCode().is4xxClientError());
    }
  }
}
