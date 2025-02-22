package org.example.projectmanagementapi.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;
import org.example.projectmanagementapi.entity.Attachment;
import org.example.projectmanagementapi.entity.Task;
import org.example.projectmanagementapi.enums.NotificationType;
import org.example.projectmanagementapi.repository.AttachmentRepository;
import org.example.projectmanagementapi.repository.IssueRepository;
import org.example.projectmanagementapi.repository.TaskRepository;
import org.example.projectmanagementapi.service.impl.AttachmentClassImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

public class AttachmentServiceTest {

  @Mock private AttachmentRepository attachmentRepository;

  @Mock private TaskRepository taskRepository;

  @Mock private IssueRepository issueRepository;

  @Mock private NotificationService notificationService;

  @Mock private S3Client s3Client;

  @InjectMocks private AttachmentClassImpl attachmentService;

  @Value("${cloud.aws.s3.bucket}")
  private String bucketName;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  public void createAttachmentForTask_createsAndReturnsAttachment() {
    MultipartFile file = mock(MultipartFile.class);
    Task task = new Task();
    Attachment attachment = Attachment.builder().id(1).fileName("file.txt").build();

    when(file.isEmpty()).thenReturn(false);
    when(file.getContentType()).thenReturn("text/plain");
    when(file.getOriginalFilename()).thenReturn("file.txt");
    when(taskRepository.findById(1)).thenReturn(Optional.of(task));
    when(attachmentRepository.save(any(Attachment.class))).thenReturn(attachment);
    doNothing()
        .when(notificationService)
        .createNotification(anyString(), any(NotificationType.class));
    when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
        .thenReturn(any(PutObjectResponse.class));

    Attachment createdAttachment = attachmentService.createAttachmentForTask(file, 1);

    assertNotNull(createdAttachment);
    assertEquals("file.txt", createdAttachment.getFileName());
    verify(taskRepository, times(1)).findById(1);
    verify(attachmentRepository, times(1)).save(any(Attachment.class));
    verify(notificationService, times(1))
        .createNotification(anyString(), any(NotificationType.class));
  }

  @Test
  public void createAttachmentForTask_throwsExceptionWhenFileIsEmpty() {
    MultipartFile file = mock(MultipartFile.class);

    when(file.isEmpty()).thenReturn(true);

    assertThrows(RuntimeException.class, () -> attachmentService.createAttachmentForTask(file, 1));
    verify(taskRepository, never()).findById(anyInt());
    verify(attachmentRepository, never()).save(any(Attachment.class));
    verify(notificationService, never())
        .createNotification(anyString(), any(NotificationType.class));
  }

  @Test
  public void createAttachmentForTask_throwsExceptionWhenInvalidFileType() {
    MultipartFile file = mock(MultipartFile.class);

    when(file.isEmpty()).thenReturn(false);
    when(file.getContentType()).thenReturn("invalid/type");

    assertThrows(RuntimeException.class, () -> attachmentService.createAttachmentForTask(file, 1));
    verify(taskRepository, never()).findById(anyInt());
    verify(attachmentRepository, never()).save(any(Attachment.class));
    verify(notificationService, never())
        .createNotification(anyString(), any(NotificationType.class));
  }

  @Test
  public void createAttachmentForTask_throwsExceptionWhenTaskNotFound() {
    MultipartFile file = mock(MultipartFile.class);

    when(file.isEmpty()).thenReturn(false);
    when(file.getContentType()).thenReturn("text/plain");
    when(taskRepository.findById(1)).thenReturn(Optional.empty());

    assertThrows(RuntimeException.class, () -> attachmentService.createAttachmentForTask(file, 1));
    verify(taskRepository, times(1)).findById(1);
    verify(attachmentRepository, never()).save(any(Attachment.class));
    verify(notificationService, never())
        .createNotification(anyString(), any(NotificationType.class));
  }

  @Test
  public void deleteAttachment_deletesAttachmentAndSendsNotification() {
    Attachment attachment = Attachment.builder().id(1).filePath("path/to/file").build();

    when(attachmentRepository.findById(1)).thenReturn(Optional.of(attachment));
    doNothing().when(s3Client).deleteObject(any(DeleteObjectRequest.class));
    doNothing()
        .when(notificationService)
        .createNotification(anyString(), any(NotificationType.class));

    attachmentService.deleteAttachment(1);

    verify(attachmentRepository, times(1)).findById(1);
    verify(attachmentRepository, times(1)).delete(any(Attachment.class));
    verify(s3Client, times(1)).deleteObject(any(DeleteObjectRequest.class));
    verify(notificationService, times(1))
        .createNotification(anyString(), any(NotificationType.class));
  }

  @Test
  public void deleteAttachment_throwsExceptionWhenAttachmentNotFound() {
    when(attachmentRepository.findById(1)).thenReturn(Optional.empty());

    assertThrows(RuntimeException.class, () -> attachmentService.deleteAttachment(1));
    verify(attachmentRepository, times(1)).findById(1);
    verify(attachmentRepository, never()).delete(any(Attachment.class));
    verify(s3Client, never()).deleteObject(any(DeleteObjectRequest.class));
    verify(notificationService, never())
        .createNotification(anyString(), any(NotificationType.class));
  }
}
