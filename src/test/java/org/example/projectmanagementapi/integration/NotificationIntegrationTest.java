package org.example.projectmanagementapi.integration;

import org.example.projectmanagementapi.config.TestJpaConfig;
import org.example.projectmanagementapi.dto.response.NotificationDto;
import org.example.projectmanagementapi.enums.NotificationType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {
      "spring.main.allow-bean-definition-overriding=true",
    })
@ActiveProfiles("test")
@Import(TestJpaConfig.class)
@DisplayName("Notification API Integration Tests")
public class NotificationIntegrationTest {

  @Autowired private TestRestTemplate restTemplate;

  @LocalServerPort private int port;

  private String getBaseUrl() {
    return "http://localhost:" + port + "/api/v1/notifications";
  }

  @Nested
  @DisplayName("Notification GET Operations")
  class NotificationGetOperations {
    @Test
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Get all notifications")
    public void testGetAllNotifications() {
      ResponseEntity<List<NotificationDto>> response =
          restTemplate.exchange(
              getBaseUrl(),
              HttpMethod.GET,
              null,
              new ParameterizedTypeReference<List<NotificationDto>>() {});
      System.out.println(response);
      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertNotNull(response.getBody());
      assertEquals(5, response.getBody().size());

      NotificationDto firstNotification = response.getBody().get(0);
      assertEquals("Your task has been updated.", firstNotification.getMessage());
      assertEquals(NotificationType.UPDATE, firstNotification.getType());
      assertFalse(firstNotification.getIsRead());
    }

    @Test
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Check notification content details")
    public void testNotificationContent() {
      ResponseEntity<List<NotificationDto>> response =
          restTemplate.exchange(
              getBaseUrl(),
              HttpMethod.GET,
              null,
              new ParameterizedTypeReference<List<NotificationDto>>() {});

      List<NotificationDto> notifications = response.getBody();
      assertNotNull(notifications);

      assertTrue(notifications.stream().anyMatch(n -> n.getType() == NotificationType.UPDATE));
      assertTrue(notifications.stream().anyMatch(n -> n.getType() == NotificationType.ASSIGNMENT));
      assertTrue(notifications.stream().anyMatch(n -> n.getType() == NotificationType.CREATION));
      assertTrue(notifications.stream().anyMatch(n -> n.getType() == NotificationType.COMPLETION));

      long readCount = notifications.stream().filter(NotificationDto::getIsRead).count();
      long unreadCount = notifications.stream().filter(n -> !n.getIsRead()).count();
      assertEquals(2, readCount);
      assertEquals(3, unreadCount);
    }

    @Test
    @Sql({"/schema.sql"})
    @DisplayName("Get notifications with empty database")
    public void testGetNotificationsEmptyDb() {
      ResponseEntity<List<NotificationDto>> response =
          restTemplate.exchange(
              getBaseUrl(),
              HttpMethod.GET,
              null,
              new ParameterizedTypeReference<List<NotificationDto>>() {});

      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertNotNull(response.getBody());
      assertTrue(response.getBody().isEmpty());
    }
  }

  @Nested
  @DisplayName("Notification Edge Cases")
  class NotificationEdgeCases {
    @Test
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Test invalid URL path")
    public void testInvalidUrlPath() {
      ResponseEntity<String> response =
          restTemplate.getForEntity(getBaseUrl() + "/nonexistent", String.class);
      assertTrue(response.getStatusCode().is4xxClientError());
    }
  }
}
