package org.example.projectmanagementapi.integration;

import org.example.projectmanagementapi.config.TestJpaConfig;
import org.example.projectmanagementapi.enums.NotificationType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest(
        properties = {"spring.main.allow-bean-definition-overriding=true"}
)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestJpaConfig.class)
@DisplayName("Notification API Integration Tests")
public class NotificationIntegrationTest {

  @Autowired private MockMvc mockMvc;

  private String getBaseUrl() {
    return "/api/v1/notifications";
  }

  @Nested
  @DisplayName("Notification GET Operations")
  class NotificationGetOperations {
    @Test
    @WithMockUser(roles = "USER")
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Get all notifications")
    public void testGetAllNotifications() throws Exception {
      mockMvc.perform(get(getBaseUrl()))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$", hasSize(5)))
              .andExpect(jsonPath("$[0].message").value("Your task has been updated."))
              .andExpect(jsonPath("$[0].type").value("UPDATE"))
              .andExpect(jsonPath("$[0].isRead").value(false));
    }

    @Test
    @WithMockUser(roles = "USER")
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Check notification content details")
    public void testNotificationContent() throws Exception {
      mockMvc.perform(get(getBaseUrl()))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$[?(@.type == 'UPDATE')]").exists())
              .andExpect(jsonPath("$[?(@.type == 'ASSIGNMENT')]").exists())
              .andExpect(jsonPath("$[?(@.type == 'CREATION')]").exists())
              .andExpect(jsonPath("$[?(@.type == 'COMPLETION')]").exists())
              // Check for read notifications
              .andExpect(jsonPath("$[?(@.isRead == true)]", hasSize(2)))
              // Check for unread notifications
              .andExpect(jsonPath("$[?(@.isRead == false)]", hasSize(3)));
    }

    @Test
    @WithMockUser(roles = "USER")
    @Sql({"/schema.sql"})
    @DisplayName("Get notifications with empty database")
    public void testGetNotificationsEmptyDb() throws Exception {
      mockMvc.perform(get(getBaseUrl()))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$", hasSize(0)));
    }
  }

  @Nested
  @DisplayName("Notification Edge Cases")
  class NotificationEdgeCases {
    @Test
    @WithMockUser(roles = "USER")
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Test invalid URL path")
    public void testInvalidUrlPath() throws Exception {
      mockMvc.perform(get(getBaseUrl() + "/nonexistent"))
              .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(roles = "USER")
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Mark non-existent notification as read")
    public void testMarkNonExistentNotificationAsRead() throws Exception {
      mockMvc.perform(get(getBaseUrl() + "/999/mark-read"))
              .andExpect(status().is4xxClientError());
    }
  }
}