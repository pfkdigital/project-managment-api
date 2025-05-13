package org.example.projectmanagementapi.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.projectmanagementapi.annotation.RateLimitedRestController;
import org.example.projectmanagementapi.dto.request.IssueRequestDto;
import org.example.projectmanagementapi.dto.response.DetailedIssueDto;
import org.example.projectmanagementapi.dto.response.IssueDto;
import org.example.projectmanagementapi.service.impl.IssueServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RateLimitedRestController
@RequestMapping("/api/v1/issues")
@RequiredArgsConstructor
public class IssueController {

  private final IssueServiceImpl issueService;

  @PostMapping
  public ResponseEntity<IssueDto> createIssue(@Valid @RequestBody IssueRequestDto issueRequestDto) {
    return new ResponseEntity<>(issueService.createIssue(issueRequestDto), HttpStatus.CREATED);
  }

  @GetMapping
  public ResponseEntity<List<IssueDto>> getAllIssues() {
    return new ResponseEntity<>(issueService.getAllIssues(), HttpStatus.OK);
  }

  @GetMapping("/{issueId}")
  public ResponseEntity<DetailedIssueDto> getIssue(@PathVariable Integer issueId) {
    return new ResponseEntity<>(issueService.getIssue(issueId), HttpStatus.OK);
  }

  @PutMapping("/{issueId}")
  public ResponseEntity<DetailedIssueDto> updateIssue(
      @PathVariable Integer issueId, @Valid @RequestBody IssueRequestDto issueRequestDto) {
    return new ResponseEntity<>(issueService.updateIssue(issueId, issueRequestDto), HttpStatus.OK);
  }

  @DeleteMapping("/{issueId}")
  public ResponseEntity<?> deleteIssue(@PathVariable Integer issueId) {
    issueService.deleteIssue(issueId);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
