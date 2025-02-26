package org.example.projectmanagementapi.service;

import org.example.projectmanagementapi.dto.response.AttachmentDto;
import org.springframework.web.multipart.MultipartFile;

public interface AttachmentService {
  AttachmentDto createAttachmentForTask(MultipartFile file, Integer taskId);

  AttachmentDto createAttachmentForIssue(MultipartFile file, Integer issueId);

  void deleteAttachment(Integer attachmentId);
}
