package org.example.projectmanagementapi.integration;

import jakarta.transaction.Transactional;
import org.example.projectmanagementapi.dto.request.ProjectRequestDto;
import org.example.projectmanagementapi.dto.response.DetailedProjectDto;
import org.example.projectmanagementapi.dto.response.ProjectWithCollaboratorsDto;
import org.example.projectmanagementapi.enums.ProjectStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class ProjectsIntegrationTest {

  @Container
  public static final PostgreSQLContainer<?> postgreSQLContainer =
      new PostgreSQLContainer<>("postgres:latest");

  @DynamicPropertySource
  static void configureDataSource(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
    registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
    registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
  }

  @LocalServerPort private int port;

  @Autowired private TestRestTemplate restTemplate;

  @Test
  @Transactional
  public void testCreateProject() {
    String url = "http://localhost:" + port + "/api/v1/projects";

    ProjectRequestDto projectRequestDto = new ProjectRequestDto();
    projectRequestDto.setName("Test Project");
    projectRequestDto.setDescription("Test Description");
    projectRequestDto.setStatus(ProjectStatus.ACTIVE);
    projectRequestDto.setOwnerId(1); // Make sure this user exists in your test DB
    projectRequestDto.setDisplayImageUrl("http://test.com/image.jpg");

    ProjectWithCollaboratorsDto response =
        restTemplate.postForObject(url, projectRequestDto, ProjectWithCollaboratorsDto.class);

    assertNotNull(response);
    assertEquals("Test Project", response.getName());
    assertEquals("Test Description", response.getDescription());
  }

  @Test
  public void testGetAllProjects() {
    String url = "http://localhost:" + port + "/api/v1/projects";
    ProjectWithCollaboratorsDto[] projects =
        restTemplate.getForObject(url, ProjectWithCollaboratorsDto[].class);

    assertNotNull(projects);
    assertEquals(1, projects.length); // Make sure reset.sql has 1 project
    assertEquals("Project Alpha", projects[0].getName());
  }

  @Test
  public void testGetProjectById() {
    String url = "http://localhost:" + port + "/api/v1/projects/1";
    DetailedProjectDto projectDto = restTemplate.getForObject(url, DetailedProjectDto.class);

    assertNotNull(projectDto);
    assertEquals("Project Alpha", projectDto.getName()); // Match the seed data
  }

  @Test
  public void testGetByProjectIdWhenProjectDoesNotExist() {
    String url = "http://localhost:" + port + "/api/v1/projects/100";
    DetailedProjectDto projectDto = restTemplate.getForObject(url, DetailedProjectDto.class);

    assertNotNull(projectDto);
    assertEquals(null, projectDto.getName());
  }

  @Test
  public void testUpdateProject() throws URISyntaxException {
    String url = "http://localhost:" + port + "/api/v1/projects/1";
    ProjectRequestDto projectRequestDto = new ProjectRequestDto();
    projectRequestDto.setName("Updated Project");
    projectRequestDto.setDescription("Updated Description");
    projectRequestDto.setStatus(ProjectStatus.COMPLETED);
    projectRequestDto.setOwnerId(1);

    URI uri = new URI(url);

    restTemplate.put(uri, projectRequestDto);

    DetailedProjectDto projectDto = restTemplate.getForObject(url, DetailedProjectDto.class);

    assertNotNull(projectDto);
    assertEquals("Updated Project", projectDto.getName());
    assertEquals("Updated Description", projectDto.getDescription());
    assertEquals(ProjectStatus.COMPLETED, projectDto.getStatus());
  }

  @Test
  public void testDeleteProject() {
    String url = "http://localhost:" + port + "/api/v1/projects/1";
    restTemplate.delete(url);

    ProjectWithCollaboratorsDto[] projects =
        restTemplate.getForObject(
            "http://localhost:" + port + "/api/v1/projects", ProjectWithCollaboratorsDto[].class);

    assertNotNull(projects);
    assertEquals(0, projects.length);
  }
}
