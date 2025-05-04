package org.example.projectmanagementapi.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

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
import org.example.projectmanagementapi.service.impl.AttachmentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.http.SdkHttpResponse;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.DeleteObjectResponse;

@ExtendWith(MockitoExtension.class)
class AttachmentServiceTest {

  @Mock private AttachmentRepository attachmentRepository;
  @Mock private TaskRepository taskRepository;
  @Mock private IssueRepository issueRepository;
  @Mock private NotificationService notificationService;
  @Mock private AmazonS3Service amazonS3Service;
  @Mock private AttachmentMapper attachmentMapper;
  @Mock private UserRepository userRepository;

  @Mock private MultipartFile mockFile;
  @Mock private PutObjectResponse putObjectResponse;
  @Mock private DeleteObjectResponse deleteObjectResponse;
  @Mock private SdkHttpResponse sdkHttpResponse;

  @InjectMocks private AttachmentServiceImpl attachmentService;

  private Task task;
  private Issue issue;
  private User user;
  private Attachment attachment;
  private AttachmentDto attachmentDto;

  @BeforeEach
  void setUp() {
    // Set values via reflection for properties annotated with @Value
    ReflectionTestUtils.setField(attachmentService, "bucketName", "test-bucket");
    ReflectionTestUtils.setField(attachmentService, "region", "us-east-1");

    // Initialize test data
    user = new User();
    user.setId(1);
    user.setUsername("testUser");

    task = new Task();
    task.setId(1);
    task.setDescription("Test Task");

    issue = new Issue();
    issue.setId(1);
    issue.setTitle("Test Issue");

    attachment = Attachment.builder()
            .id(1)
            .fileName("test-file.jpg")
            .fileType(AcceptedFileType.JPEG.getMediaType().toString())
            .filePath("https://test-bucket.s3.us-east-1.amazonaws.com/attachments/tasks/1/test-file.jpg")
            .uploadedAt(LocalDate.now())
            .author(user)
            .build();

    attachmentDto = new AttachmentDto();
    attachmentDto.setId(1);
    attachmentDto.setFileName("test-file.jpg");
    attachmentDto.setFilePath("https://test-bucket.s3.us-east-1.amazonaws.com/attachments/tasks/1/test-file.jpg");
  }

  @Test
  void createAttachmentForTask_createsAndReturnsAttachment() throws IOException {
    // Arrange
    when(mockFile.isEmpty()).thenReturn(false);
    when(mockFile.getContentType()).thenReturn(AcceptedFileType.JPEG.getMediaType().toString());
    when(mockFile.getOriginalFilename()).thenReturn("test-file.jpg");
    when(taskRepository.findById(1)).thenReturn(Optional.of(task));
    when(userRepository.findById(1)).thenReturn(Optional.of(user));
    doNothing().when(amazonS3Service).uploadObject(any(MultipartFile.class), anyString());
    when(attachmentRepository.save(any(Attachment.class))).thenReturn(attachment);
    when(attachmentMapper.toAttachmentDto(any(Attachment.class))).thenReturn(attachmentDto);

    // Act
    AttachmentDto result = attachmentService.createAttachmentForTask(mockFile, 1);

    // Assert
    assertNotNull(result);
    assertEquals("test-file.jpg", result.getFileName());

    verify(mockFile).isEmpty();
    verify(mockFile, times(2)).getContentType();
    verify(mockFile, times(2)).getOriginalFilename();
    verify(taskRepository).findById(1);
    verify(userRepository).findById(1);
    verify(amazonS3Service).uploadObject(any(MultipartFile.class), anyString());
    verify(attachmentRepository).save(any(Attachment.class));
    verify(notificationService).createNotification(anyString(), eq(NotificationType.UPDATE));
    verify(attachmentMapper).toAttachmentDto(any(Attachment.class));
    verify(taskRepository).save(task);
  }

  @Test
  void createAttachmentForTask_throwsExceptionWhenTaskNotFound() {
    // Arrange
    when(mockFile.isEmpty()).thenReturn(false);
    when(mockFile.getContentType()).thenReturn(AcceptedFileType.JPEG.getMediaType().toString());
    when(taskRepository.findById(1)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(RuntimeException.class, () -> attachmentService.createAttachmentForTask(mockFile, 1));

    verify(mockFile).isEmpty();
    verify(mockFile).getContentType();
    verify(taskRepository).findById(1);
    verify(amazonS3Service, never()).uploadObject(any(MultipartFile.class), anyString());
    verify(attachmentRepository, never()).save(any(Attachment.class));
  }

  @Test
  void createAttachmentForIssue_createsAndReturnsAttachment() throws IOException {
    // Arrange
    when(mockFile.isEmpty()).thenReturn(false);
    when(mockFile.getContentType()).thenReturn(AcceptedFileType.JPEG.getMediaType().toString());
    when(mockFile.getOriginalFilename()).thenReturn("test-file.jpg");
    when(issueRepository.findById(1)).thenReturn(Optional.of(issue));
    when(userRepository.findById(1)).thenReturn(Optional.of(user));
    doNothing().when(amazonS3Service).uploadObject(any(MultipartFile.class), anyString());
    when(attachmentRepository.save(any(Attachment.class))).thenReturn(attachment);
    when(attachmentMapper.toAttachmentDto(any(Attachment.class))).thenReturn(attachmentDto);

    // Act
    AttachmentDto result = attachmentService.createAttachmentForIssue(mockFile, 1);

    // Assert
    assertNotNull(result);
    assertEquals("test-file.jpg", result.getFileName());

    verify(mockFile).isEmpty();
    verify(mockFile, times(2)).getContentType();
    verify(mockFile, times(2)).getOriginalFilename();
    verify(issueRepository).findById(1);
    verify(userRepository).findById(1);
    verify(amazonS3Service).uploadObject(any(MultipartFile.class), anyString());
    verify(attachmentRepository).save(any(Attachment.class));
    verify(notificationService).createNotification(anyString(), eq(NotificationType.UPDATE));
    verify(attachmentMapper).toAttachmentDto(any(Attachment.class));
    verify(issueRepository).save(issue);
  }

  @Test
  void createAttachmentForIssue_throwsExceptionWhenIssueNotFound() {
    // Arrange
    when(mockFile.isEmpty()).thenReturn(false);
    when(mockFile.getContentType()).thenReturn(AcceptedFileType.JPEG.getMediaType().toString());
    when(issueRepository.findById(1)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(RuntimeException.class, () -> attachmentService.createAttachmentForIssue(mockFile, 1));

    verify(mockFile).isEmpty();
    verify(mockFile).getContentType();
    verify(issueRepository).findById(1);
    verify(amazonS3Service, never()).uploadObject(any(MultipartFile.class), anyString());
    verify(attachmentRepository, never()).save(any(Attachment.class));
  }

  @Test
  void deleteAttachment_deletesAttachmentForTask() {
    // Arrange
    attachment.setTask(task);

    when(attachmentRepository.findById(1)).thenReturn(Optional.of(attachment));
    when(deleteObjectResponse.sdkHttpResponse()).thenReturn(sdkHttpResponse);
    when(sdkHttpResponse.isSuccessful()).thenReturn(true);
    when(amazonS3Service.deleteObject(anyString())).thenReturn(deleteObjectResponse);

    // Act
    attachmentService.deleteAttachment(1);

    // Assert
    verify(attachmentRepository).findById(1);
    verify(amazonS3Service).deleteObject(anyString());
    verify(sdkHttpResponse).isSuccessful();
    verify(attachmentRepository).delete(attachment);
    verify(notificationService).createNotification(anyString(), eq(NotificationType.DESTRUCTION));
  }

  @Test
  void deleteAttachment_deletesAttachmentForIssue() {
    // Arrange
    attachment.setIssue(issue);

    when(attachmentRepository.findById(1)).thenReturn(Optional.of(attachment));
    when(deleteObjectResponse.sdkHttpResponse()).thenReturn(sdkHttpResponse);
    when(sdkHttpResponse.isSuccessful()).thenReturn(true);
    when(amazonS3Service.deleteObject(anyString())).thenReturn(deleteObjectResponse);

    // Act
    attachmentService.deleteAttachment(1);

    // Assert
    verify(attachmentRepository).findById(1);
    verify(amazonS3Service).deleteObject(anyString());
    verify(sdkHttpResponse).isSuccessful();
    verify(attachmentRepository).delete(attachment);
    verify(notificationService).createNotification(anyString(), eq(NotificationType.DESTRUCTION));
  }

  @Test
  void deleteAttachment_throwsExceptionWhenAttachmentNotFound() {
    // Arrange
    when(attachmentRepository.findById(1)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(RuntimeException.class, () -> attachmentService.deleteAttachment(1));
    verify(attachmentRepository).findById(1);
    verify(amazonS3Service, never()).deleteObject(anyString());
    verify(attachmentRepository, never()).delete(any(Attachment.class));
  }

  @Test
  void validateFile_throwsExceptionWhenFileIsEmpty() {
    // Arrange
    when(mockFile.isEmpty()).thenReturn(true);

    // Act & Assert
    assertThrows(RuntimeException.class, () -> attachmentService.createAttachmentForTask(mockFile, 1));
    verify(mockFile).isEmpty();
    verify(amazonS3Service, never()).uploadObject(any(MultipartFile.class), anyString());
  }

  @Test
  void validateFile_throwsExceptionWhenInvalidFileType() {
    // Arrange
    when(mockFile.isEmpty()).thenReturn(false);
    when(mockFile.getContentType()).thenReturn("application/unknown");

    // Act & Assert
    assertThrows(RuntimeException.class, () -> attachmentService.createAttachmentForTask(mockFile, 1));
    verify(mockFile).isEmpty();
    verify(mockFile, atLeastOnce()).getContentType();
    verify(amazonS3Service, never()).uploadObject(any(MultipartFile.class), anyString());
  }
}