package org.example.projectmanagementapi.controller;

import lombok.RequiredArgsConstructor;
import org.example.projectmanagementapi.annotation.RateLimitedRestController;
import org.example.projectmanagementapi.annotation.WithRateLimitProtection;
import org.example.projectmanagementapi.dto.response.AttachmentDto;
import org.example.projectmanagementapi.service.AttachmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RateLimitedRestController
@RequestMapping("/api/v1/attachments")
@RequiredArgsConstructor
public class AttachmentController {

  private final AttachmentService attachmentService;

  @PostMapping("/task/{taskId}")
  public ResponseEntity<AttachmentDto> createAttachmentForTask(
      @RequestBody MultipartFile file, @PathVariable Integer taskId) {
    return new ResponseEntity<>(
        attachmentService.createAttachmentForTask(file, taskId), HttpStatus.CREATED);
  }

  @PostMapping("/issue/{issueId}")
  public ResponseEntity<AttachmentDto> createAttachmentForIssue(
      @RequestBody MultipartFile file, @PathVariable Integer issueId) {
    return new ResponseEntity<>(
        attachmentService.createAttachmentForIssue(file, issueId), HttpStatus.CREATED);
  }

  @DeleteMapping("/{attachmentId}")
  public ResponseEntity<?> deleteAttachment(@PathVariable Integer attachmentId) {
    attachmentService.deleteAttachment(attachmentId);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
