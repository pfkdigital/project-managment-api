package org.example.projectmanagementapi.controller;

import lombok.RequiredArgsConstructor;
import org.example.projectmanagementapi.dto.IssueDto;
import org.example.projectmanagementapi.service.impl.IssueServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/issues")
@RequiredArgsConstructor
public class IssueController {

  private final IssueServiceImpl issueService;

  @PostMapping
  public ResponseEntity<?> createIssue(@RequestBody IssueDto issueDto) {
    return new ResponseEntity<>(issueService.createIssue(issueDto), HttpStatus.CREATED);
  }

  @GetMapping
  public ResponseEntity<?> getAllIssues() {
    return new ResponseEntity<>(issueService.getAllIssues(), HttpStatus.OK);
  }

  @GetMapping("/{issueId}")
  public ResponseEntity<?> getIssue(@PathVariable Integer issueId) {
    return new ResponseEntity<>(issueService.getIssue(issueId), HttpStatus.OK);
  }

  @PutMapping("/{issueId}")
  public ResponseEntity<?> updateIssue(
      @PathVariable Integer issueId, @RequestBody IssueDto issueDto) {
    return new ResponseEntity<>(issueService.updateIssue(issueId, issueDto), HttpStatus.OK);
  }

  @DeleteMapping("/{issueId}")
  public ResponseEntity<?> deleteIssue(@PathVariable Integer issueId) {
    issueService.deleteIssue(issueId);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
