package org.example.projectmanagementapi.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.example.projectmanagementapi.entity.Attachment;
import org.example.projectmanagementapi.entity.Notification;
import org.example.projectmanagementapi.entity.Task;
import org.example.projectmanagementapi.repository.AttachmentRepository;
import org.example.projectmanagementapi.repository.IssueRepository;
import org.example.projectmanagementapi.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AttachmentServiceTest {

    @Mock
    private AttachmentRepository attachmentRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private IssueRepository issueRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private AmazonS3 s3Client;

    @InjectMocks
    private AttachmentService attachmentService;

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
        doNothing().when(notificationService).createNotification(any(Notification.class));
        doNothing().when(s3Client).putObject(any(PutObjectRequest.class));

        Attachment createdAttachment = attachmentService.createAttachmentForTask(file, 1);

        assertNotNull(createdAttachment);
        assertEquals("file.txt", createdAttachment.getFileName());
        verify(taskRepository, times(1)).findById(1);
        verify(attachmentRepository, times(1)).save(any(Attachment.class));
        verify(notificationService, times(1)).createNotification(any(Notification.class));
    }

    @Test
    public void createAttachmentForTask_throwsExceptionWhenFileIsEmpty() {
        MultipartFile file = mock(MultipartFile.class);

        when(file.isEmpty()).thenReturn(true);

        assertThrows(RuntimeException.class, () -> attachmentService.createAttachmentForTask(file, 1));
        verify(taskRepository, never()).findById(anyInt());
        verify(attachmentRepository, never()).save(any(Attachment.class));
        verify(notificationService, never()).createNotification(any(Notification.class));
    }

    @Test
    public void createAttachmentForTask_throwsExceptionWhenInvalidFileType() {
        MultipartFile file = mock(MultipartFile.class);

        when(file.isEmpty()).thenReturn(false);
        when(file.getContentType()).thenReturn("invalid/type");

        assertThrows(RuntimeException.class, () -> attachmentService.createAttachmentForTask(file, 1));
        verify(taskRepository, never()).findById(anyInt());
        verify(attachmentRepository, never()).save(any(Attachment.class));
        verify(notificationService, never()).createNotification(any(Notification.class));
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
        verify(notificationService, never()).createNotification(any(Notification.class));
    }

    @Test
    public void deleteAttachment_deletesAttachmentAndSendsNotification() {
        Attachment attachment = Attachment.builder().id(1).filePath("path/to/file").build();

        when(attachmentRepository.findById(1)).thenReturn(Optional.of(attachment));
        doNothing().when(s3Client).deleteObject(anyString(), anyString());
        doNothing().when(notificationService).createNotification(any(Notification.class));

        attachmentService.deleteAttachment(1);

        verify(attachmentRepository, times(1)).findById(1);
        verify(attachmentRepository, times(1)).delete(any(Attachment.class));
        verify(s3Client, times(1)).deleteObject(anyString(), anyString());
        verify(notificationService, times(1)).createNotification(any(Notification.class));
    }

    @Test
    public void deleteAttachment_throwsExceptionWhenAttachmentNotFound() {
        when(attachmentRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> attachmentService.deleteAttachment(1));
        verify(attachmentRepository, times(1)).findById(1);
        verify(attachmentRepository, never()).delete(any(Attachment.class));
        verify(s3Client, never()).deleteObject(anyString(), anyString());
        verify(notificationService, never()).createNotification(any(Notification.class));
    }
}