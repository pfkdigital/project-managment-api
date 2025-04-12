package org.example.projectmanagementapi.integration;

import org.example.projectmanagementapi.dto.response.NotificationDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.jdbc.Sql;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
        assertNotNull(notifications);
        assertEquals(5, notifications.length);
        assertEquals("Your task has been updated.", notifications[0].getMessage());
    }
}
