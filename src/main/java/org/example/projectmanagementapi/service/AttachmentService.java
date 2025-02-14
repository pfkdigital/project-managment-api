package org.example.projectmanagementapi.service;

import org.example.projectmanagementapi.entity.Attachment;
import org.springframework.web.multipart.MultipartFile;

public interface AttachmentService {
    Attachment createAttachmentForTask(MultipartFile file, Integer taskId);
    Attachment createAttachmentForIssue(MultipartFile file, Integer issueId);
    void deleteAttachment(Integer attachmentId);
}
