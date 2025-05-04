package org.example.projectmanagementapi.service.impl;

import java.time.LocalDate;
import java.util.Arrays;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.projectmanagementapi.dto.response.AttachmentDto;
import org.example.projectmanagementapi.entity.Attachment;
import org.example.projectmanagementapi.entity.Issue;
import org.example.projectmanagementapi.entity.Task;
import org.example.projectmanagementapi.entity.User;
import org.example.projectmanagementapi.enums.AcceptedFileType;
import org.example.projectmanagementapi.enums.NotificationType;
import org.example.projectmanagementapi.mapper.AttachmentMapper;
import org.example.projectmanagementapi.repository.AttachmentRepository;
import org.example.projectmanagementapi.repository.IssueRepository;
import org.example.projectmanagementapi.repository.TaskRepository;
import org.example.projectmanagementapi.repository.UserRepository;
import org.example.projectmanagementapi.service.AmazonS3Service;
import org.example.projectmanagementapi.service.AttachmentService;
import org.example.projectmanagementapi.service.NotificationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

@Service
@RequiredArgsConstructor
public class AttachmentServiceImpl implements AttachmentService {

  @Value("${cloud.aws.s3.bucket}")
  private String bucketName;

  @Value("${cloud.aws.region.static}")
  private String region;

  private final AttachmentRepository attachmentRepository;
  private final TaskRepository taskRepository;
  private final IssueRepository issueRepository;
  private final NotificationService notificationService;
  private final AmazonS3Service amazonS3Service;
  private final AttachmentMapper attachmentMapper;
  private final UserRepository
      userRepository;

  @Override
  public AttachmentDto createAttachmentForTask(MultipartFile file, Integer taskId) {
    validateFile(file);
    Task task =
        taskRepository.findById(taskId).orElseThrow(() -> new EntityNotFoundException("Task not found with id " + taskId));
    Attachment attachment = createAndSaveAttachment(file, "tasks/" + taskId);
    task.addAttachment(attachment);
    taskRepository.save(task);
    return attachmentMapper.toAttachmentDto(attachment);
  }

  @Override
  public AttachmentDto createAttachmentForIssue(MultipartFile file, Integer issueId) {
    validateFile(file);
    Issue issue =
        issueRepository
            .findById(issueId)
            .orElseThrow(() -> new EntityNotFoundException("Issue not found with id " + issueId));
    Attachment attachment = createAndSaveAttachment(file, "issues/" + issueId);
    issue.addAttachment(attachment);
    issueRepository.save(issue);
    return attachmentMapper.toAttachmentDto(attachment);
  }

  @Override
  public void deleteAttachment(Integer attachmentId) {
    Attachment attachment =
        attachmentRepository
            .findById(attachmentId)
            .orElseThrow(() -> new EntityNotFoundException("Attachment not found with id " + attachmentId));
    String key =
        "attachments/"
            + (attachment.getTask() != null ? "tasks/" : "issues/")
            + (attachment.getTask() != null
                ? attachment.getTask().getId()
                : attachment.getIssue().getId())
            + "/"
            + attachment.getFileName();
    if (amazonS3Service.deleteObject(key).sdkHttpResponse().isSuccessful()) {
      attachmentRepository.delete(attachment);
      notificationService.createNotification("Attachment deleted", NotificationType.DESTRUCTION);
    }
  }

  private Attachment createAndSaveAttachment(MultipartFile file, String path) {
    // User currentUser = (User)
    // SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User user =
        userRepository.findById(1).orElseThrow(() -> new EntityNotFoundException("User not found with id 1")); // Hardcoded for now
    Attachment attachment =
        Attachment.builder()
            .fileName(file.getOriginalFilename())
            .fileType(file.getContentType())
            .author(user)
            .build();
    uploadFileToS3(file, path, attachment);
    notificationService.createNotification("Attachment created", NotificationType.UPDATE);
    return attachmentRepository.save(attachment);
  }

  private void uploadFileToS3(MultipartFile file, String path, Attachment attachment) {
    String keyName = "attachments/" + path + "/" + file.getOriginalFilename();
    amazonS3Service.uploadObject(file, keyName);
    attachment.setFilePath(
        String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, keyName));
    attachment.setUploadedAt(LocalDate.now());
  }

  private void validateFile(MultipartFile file) {
    if (file.isEmpty() || !isValidFileType(file)) {
      throw new RuntimeException("Invalid file");
    }
  }

  private boolean isValidFileType(MultipartFile file) {
    return Arrays.stream(AcceptedFileType.values())
        .anyMatch(type -> type.getMediaType().toString().equals(file.getContentType()));
  }
}
