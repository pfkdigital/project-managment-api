package org.example.projectmanagementapi.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;
import org.example.projectmanagementapi.dto.request.IssueRequestDto;
import org.example.projectmanagementapi.dto.response.DetailedIssueDto;
import org.example.projectmanagementapi.dto.response.IssueDto;
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
    IssueDto issueDto = new IssueDto();
    when(issueService.createIssue(any(IssueRequestDto.class))).thenReturn(issueDto);

    ResponseEntity<?> response = issueController.createIssue(new IssueRequestDto());

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(issueDto, response.getBody());
  }

  @Test
  void testGetIssues() {
    List<IssueDto> issues = Collections.emptyList();
    when(issueService.getAllIssues()).thenReturn(issues);

    ResponseEntity<?> response = issueController.getAllIssues();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(issues, response.getBody());
  }

  @Test
  void testGetIssue() {
    DetailedIssueDto issueDto = new DetailedIssueDto();
    when(issueService.getIssue(anyInt())).thenReturn(issueDto);

    ResponseEntity<?> response = issueController.getIssue(1);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(issueDto, response.getBody());
  }

  @Test
  void testUpdateIssue() {
    DetailedIssueDto issueDto = new DetailedIssueDto();
    when(issueService.updateIssue(anyInt(), any(IssueRequestDto.class))).thenReturn(issueDto);

    ResponseEntity<?> response = issueController.updateIssue(1, new IssueRequestDto());

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(issueDto, response.getBody());
  }

  @Test
  void testDeleteIssue() {
    doNothing().when(issueService).deleteIssue(anyInt());

    ResponseEntity<?> response = issueController.deleteIssue(1);

    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    verify(issueService, times(1)).deleteIssue(1);
  }
}