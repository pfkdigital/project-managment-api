package org.example.projectmanagementapi.integration;

import org.example.projectmanagementapi.config.TestJpaConfig;
import org.example.projectmanagementapi.dto.request.ProjectRequestDto;
import org.example.projectmanagementapi.enums.ProjectStatus;
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

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {"spring.main.allow-bean-definition-overriding=true"})
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestJpaConfig.class)
@DisplayName("Project API Integration Tests")
public class ProjectsIntegrationTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  private String getBaseUrl() {
    return "/api/v1/projects";
  }

  @Nested
  @DisplayName("Project CRUD Operations")
  class ProjectCrudTests {
    @Test
    @WithMockUser(roles = "USER")
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Create a new project")
    public void testCreateProject() throws Exception {
      ProjectRequestDto projectRequestDto = new ProjectRequestDto();
      projectRequestDto.setName("Test Project");
      projectRequestDto.setDescription("Test Description");
      projectRequestDto.setStatus(ProjectStatus.ACTIVE);
      projectRequestDto.setDisplayImageUrl("http://example.com/image.png");
      projectRequestDto.setOwnerId(1);

      mockMvc
          .perform(
              post(getBaseUrl())
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(projectRequestDto)))
          .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "USER")
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Get all projects")
    public void testGetAllProjects() throws Exception {
      mockMvc
          .perform(get(getBaseUrl()))
          .andExpect(status().isOk())
          .andExpect(content().string(containsString("Project Alpha")));
    }

    @Test
    @WithMockUser(roles = "USER")
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Get project by ID")
    public void testGetProjectById() throws Exception {
      mockMvc
          .perform(get(getBaseUrl() + "/1"))
          .andExpect(status().isOk())
          .andExpect(content().string(containsString("Project Alpha")));
    }

    @Test
    @WithMockUser(roles = "USER")
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Update a project")
    public void testUpdateProject() throws Exception {
      ProjectRequestDto projectRequestDto = new ProjectRequestDto();
      projectRequestDto.setName("Updated Project Alpha");
      projectRequestDto.setDescription("Updated first test project");
      projectRequestDto.setStatus(ProjectStatus.ACTIVE);
      projectRequestDto.setDisplayImageUrl("http://example.com/updated_image.png");
      projectRequestDto.setOwnerId(1);

      mockMvc
          .perform(
              put(getBaseUrl() + "/1")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(projectRequestDto)))
          .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Delete a project")
    public void testDeleteProject() throws Exception {
      mockMvc.perform(delete(getBaseUrl() + "/1")).andExpect(status().isOk());

      mockMvc.perform(get(getBaseUrl() + "/1")).andExpect(status().is4xxClientError());
    }
  }

  @Nested
  @DisplayName("Project Collaborator Operations")
  class ProjectCollaboratorTests {
    @Test
    @WithMockUser(roles = "USER")
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Add collaborator to a project")
    public void testAddCollaborator() throws Exception {
      mockMvc.perform(patch(getBaseUrl() + "/1/collaborators/4")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Remove collaborator from a project")
    public void testRemoveCollaborator() throws Exception {
      mockMvc.perform(delete(getBaseUrl() + "/1/collaborators/2")).andExpect(status().isOk());
    }
  }

  @Nested
  @DisplayName("Project Error Cases")
  class ProjectErrorTests {
    @Test
    @WithMockUser(roles = "USER")
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Get non-existent project")
    public void testGetNonExistentProject() throws Exception {
      mockMvc.perform(get(getBaseUrl() + "/999")).andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(roles = "USER")
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Create project with missing required fields")
    public void testCreateProjectWithMissingFields() throws Exception {
      ProjectRequestDto projectRequestDto = new ProjectRequestDto();
      // Missing required fields

      mockMvc
          .perform(
              post(getBaseUrl())
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(projectRequestDto)))
          .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(roles = "USER")
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Add non-existent user as collaborator")
    public void testAddNonExistentCollaborator() throws Exception {
      mockMvc
          .perform(patch(getBaseUrl() + "/1/collaborators/999"))
          .andExpect(status().is4xxClientError());
    }
  }
}
