package org.example.projectmanagementapi.integration;

import org.example.projectmanagementapi.dto.request.ProjectRequestDto;
import org.example.projectmanagementapi.dto.response.DetailedProjectDto;
import org.example.projectmanagementapi.dto.response.ProjectWithCollaboratorsDto;
import org.example.projectmanagementapi.enums.ProjectStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.jdbc.Sql;

import static org.junit.jupiter.api.Assertions.*;

public class ProjectsIntegrationTest extends BaseIntegration {

  @Autowired private TestRestTemplate restTemplate;

  @LocalServerPort private int port;

  @Test
  @Sql({"/schema.sql", "/data.sql"})
  public void testCreateProject() {
    String url = "http://localhost:" + port + "/api/v1/projects";

    ProjectRequestDto projectRequestDto = new ProjectRequestDto();
    projectRequestDto.setName("Test Project");
    projectRequestDto.setDescription("Test Description");
    projectRequestDto.setStatus(ProjectStatus.ACTIVE);
    projectRequestDto.setOwnerId(1);
    projectRequestDto.setDisplayImageUrl("https://test.com/image.jpg");

    ProjectWithCollaboratorsDto response =
        restTemplate.postForObject(url, projectRequestDto, ProjectWithCollaboratorsDto.class);
    System.out.println(response);
    assertNotNull(response);
    assertEquals("Test Project", response.getName());
    assertEquals("Test Description", response.getDescription());
  }

  @Test
  @Sql({"/schema.sql", "/data.sql"})
  public void testGetAllProjects() {
    String url = "http://localhost:" + port + "/api/v1/projects";
    ProjectWithCollaboratorsDto[] projects =
        restTemplate.getForObject(url, ProjectWithCollaboratorsDto[].class);

    assertNotNull(projects);
    assertEquals(3, projects.length);
    assertEquals("Project Alpha", projects[0].getName());
  }

  @Test
  @Sql({"/schema.sql", "/data.sql"})
  public void testGetProjectById() {
    String url = "http://localhost:" + port + "/api/v1/projects/1";
    DetailedProjectDto projectDto = restTemplate.getForObject(url, DetailedProjectDto.class);

    assertNotNull(projectDto);
    assertEquals("Project Alpha", projectDto.getName());
  }

  @Test
  @Sql({"/schema.sql", "/data.sql"})
  public void testGetByProjectIdWhenProjectDoesNotExist() {
    String url = "http://localhost:" + port + "/api/v1/projects/100";
    DetailedProjectDto projectDto = restTemplate.getForObject(url, DetailedProjectDto.class);

    assertNotNull(projectDto);
    assertEquals(null, projectDto.getName());
  }

  @Test
  @Sql({"/schema.sql", "/data.sql"})
  public void testUpdateProject() {
    String url = "http://localhost:" + port + "/api/v1/projects/1";
    ProjectRequestDto projectRequestDto = new ProjectRequestDto();
    projectRequestDto.setName("Updated Project");
    projectRequestDto.setDescription("Updated Description");
    projectRequestDto.setStatus(ProjectStatus.COMPLETED);
    projectRequestDto.setOwnerId(1);
    projectRequestDto.setDisplayImageUrl("https://test.com/updated_image.jpg");

    restTemplate.put(url, projectRequestDto, DetailedProjectDto.class);

    DetailedProjectDto updatedProject = restTemplate.getForObject(url, DetailedProjectDto.class);

    assertNotNull(updatedProject);
    assertEquals("Updated Project", updatedProject.getName());
    assertEquals("Updated Description", updatedProject.getDescription());
    assertEquals(ProjectStatus.COMPLETED, updatedProject.getStatus());
    assertEquals("https://test.com/updated_image.jpg", updatedProject.getDisplayImageUrl());
  }

  @Test
  @Sql({"/schema.sql", "/data.sql"})
  public void testDeleteProject() {
    String url = "http://localhost:" + port + "/api/v1/projects/1";
    ProjectWithCollaboratorsDto[] projectsBeforeDelete =
        restTemplate.getForObject(
            "http://localhost:" + port + "/api/v1/projects", ProjectWithCollaboratorsDto[].class);
    restTemplate.delete(url);

    ProjectWithCollaboratorsDto[] projectsAfterDelete =
        restTemplate.getForObject(
            "http://localhost:" + port + "/api/v1/projects", ProjectWithCollaboratorsDto[].class);

    assertNotNull(projectsAfterDelete);
    assertNotEquals(projectsBeforeDelete.length, projectsAfterDelete.length);
    assertEquals(2, projectsAfterDelete.length);
  }

  @Test
  @Sql({"/schema.sql", "/data.sql"})
  public void testAddProjectMember() {
    String patchUrl = "http://localhost:" + port + "/api/v1/projects/1/collaborators/2";
    String getUrl = "http://localhost:" + port + "/api/v1/projects/1";
    restTemplate.patchForObject(patchUrl, null, DetailedProjectDto.class);

    DetailedProjectDto projectDto = restTemplate.getForObject(getUrl, DetailedProjectDto.class);

    assertNotNull(projectDto);
    assertEquals(3, projectDto.getCollaborators().size());
  }

  @Test
  @Sql({"/schema.sql", "/data.sql"})
  public void testRemoveProjectMember() {
    String patchUrl = "http://localhost:" + port + "/api/v1/projects/1/collaborators/2";
    String getUrl = "http://localhost:" + port + "/api/v1/projects/1";
    restTemplate.delete(patchUrl,null,Void.class);

    DetailedProjectDto projectDto = restTemplate.getForObject(getUrl, DetailedProjectDto.class);

    assertNotNull(projectDto);
    assertEquals(1, projectDto.getCollaborators().size());
  }
}
