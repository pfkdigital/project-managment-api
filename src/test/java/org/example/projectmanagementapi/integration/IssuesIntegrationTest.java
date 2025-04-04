package org.example.projectmanagementapi.integration;

import org.example.projectmanagementapi.dto.request.IssueRequestDto;
import org.example.projectmanagementapi.dto.response.DetailedIssueDto;
import org.example.projectmanagementapi.dto.response.IssueDto;
import org.example.projectmanagementapi.enums.IssueStatus;
import org.example.projectmanagementapi.enums.PriorityStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.jdbc.Sql;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class IssuesIntegrationTest extends BaseIntegration {
    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    @Test
    @Sql({"/schema.sql", "/data.sql"})
    public void testCreateIssue() {
        String url = "http://localhost:" + port + "/api/v1/issues";

        // Create an IssueRequestDto object and set its properties
        IssueRequestDto issueRequestDto = new IssueRequestDto();
        issueRequestDto.setTitle("Test Issue");
        issueRequestDto.setDescription("Test Description");
        issueRequestDto.setStatus(IssueStatus.OPEN);
        issueRequestDto.setPriorityStatus(PriorityStatus.HIGH);
        issueRequestDto.setProjectId(1);
        issueRequestDto.setReportedById(1);
        issueRequestDto.setAssignedToId(2);

        // Send a POST request to create the issue
        IssueDto response = restTemplate.postForObject(url, issueRequestDto, IssueDto.class);

        // Assertions to verify the response
        assertNotNull(response);
        assertEquals("Test Issue", response.getTitle());
    }

    @Test
    @Sql({"/schema.sql", "/data.sql"})
    public void testGetAllIssues() {
        String url = "http://localhost:" + port + "/api/v1/issues";
        IssueDto[] issues = restTemplate.getForObject(url, IssueDto[].class);

        // Assertions to verify the response
        assertNotNull(issues);
        assertEquals(4, issues.length);
        assertEquals("Issue 1", issues[0].getTitle());
    }

    @Test
    @Sql({"/schema.sql", "/data.sql"})
    public void testGetIssueById() {
        String url = "http://localhost:" + port + "/api/v1/issues/1";
        DetailedIssueDto issueDto = restTemplate.getForObject(url, DetailedIssueDto.class);

        // Assertions to verify the response
        assertNotNull(issueDto);
        assertEquals("Issue 1", issueDto.getTitle());
    }

    @Test
    @Sql({"/schema.sql", "/data.sql"})
    public void testDeleteIssue() {
        String url = "http://localhost:" + port + "/api/v1/issues/1";

        // Send a DELETE request to delete the issue
        restTemplate.delete(url);

        // Verify that the issue is deleted by trying to fetch it
        IssueDto issueDto = restTemplate.getForObject(url, IssueDto.class);
        assertNotNull(issueDto);
        assertEquals(null, issueDto.getTitle());
    }

    @Test
    @Sql({"/schema.sql", "/data.sql"})
    public void testUpdateIssue() {
        String url = "http://localhost:" + port + "/api/v1/issues/1";

        // Create an IssueRequestDto object and set its properties
        IssueRequestDto issueRequestDto = new IssueRequestDto();
        issueRequestDto.setTitle("Updated Issue");
        issueRequestDto.setDescription("Updated Description");
        issueRequestDto.setStatus(IssueStatus.IN_PROGRESS);
        issueRequestDto.setPriorityStatus(PriorityStatus.MEDIUM);
        issueRequestDto.setProjectId(1);
        issueRequestDto.setReportedById(1);
        issueRequestDto.setAssignedToId(2);

        // Send a PUT request to update the issue
        restTemplate.put(url, issueRequestDto, DetailedIssueDto.class);

        // Fetch the updated issue
        DetailedIssueDto response = restTemplate.getForObject(url, DetailedIssueDto.class);

        // Assertions to verify the response
        assertNotNull(response);
        assertEquals("Updated Issue", response.getTitle());
    }
}
