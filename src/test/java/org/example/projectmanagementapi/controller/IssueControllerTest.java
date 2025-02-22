package org.example.projectmanagementapi.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Collections;
import org.example.projectmanagementapi.dto.IssueDto;
import org.example.projectmanagementapi.entity.Issue;
import org.example.projectmanagementapi.service.impl.IssueServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class IssueControllerTest {

  @Mock private IssueServiceImpl issueService;

  @InjectMocks private IssueController issueController;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testCreateIssue() {
    Issue issue = Issue.builder().id(1).title("Issue1").description("Description1").build();
    IssueDto issueDto = new IssueDto();
    when(issueService.createIssue(any(IssueDto.class))).thenReturn(issue);

    ResponseEntity<?> response = issueController.createIssue(issueDto);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(issue, response.getBody());
  }

  @Test
  void testGetAllIssues() {

    when(issueService.getAllIssues()).thenReturn(Collections.emptyList());

    ResponseEntity<?> response = issueController.getAllIssues();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(Collections.emptyList(), response.getBody());
  }

  @Test
  void testGetIssue() {
    Issue issue = Issue.builder().id(1).title("Issue1").description("Description1").build();
    when(issueService.getIssue(anyInt())).thenReturn(issue);

    ResponseEntity<?> response = issueController.getIssue(1);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(issue, response.getBody());
  }

  @Test
  void testUpdateIssue() {
    Issue issue = Issue.builder().id(1).title("Issue1").description("Description1").build();
    IssueDto issueDto = new IssueDto();
    when(issueService.updateIssue(anyInt(), any(IssueDto.class))).thenReturn(issue);

    ResponseEntity<?> response = issueController.updateIssue(1, issueDto);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(issue, response.getBody());
  }

  @Test
  void testDeleteIssue() {
    doNothing().when(issueService).deleteIssue(anyInt());

    ResponseEntity<?> response = issueController.deleteIssue(1);

    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    verify(issueService, times(1)).deleteIssue(1);
  }
}
