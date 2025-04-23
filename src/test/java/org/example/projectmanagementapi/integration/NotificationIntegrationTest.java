package org.example.projectmanagementapi.integration;

import org.example.projectmanagementapi.dto.response.NotificationDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.jdbc.Sql;


public class NotificationIntegrationTest extends BaseIntegration {
    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;


    @Test
    @Sql({"/schema.sql", "/data.sql"})
    public void testGetAllNotifications() {
        String url = "http://localhost:" + port + "/api/v1/notifications";
        NotificationDto[] notifications = restTemplate.getForObject(url, NotificationDto[].class);

        // Assertions to verify the response
        Assertions.assertNotNull(notifications);
        Assertions.assertEquals(5, notifications.length);
        Assertions.assertEquals("Your task has been updated.", notifications[0].getMessage());
    }
}
