package org.example.projectmanagementapi.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import org.example.projectmanagementapi.dto.response.AttachmentDto;
import org.example.projectmanagementapi.entity.Attachment;
import org.example.projectmanagementapi.entity.Issue;
import org.example.projectmanagementapi.enums.AcceptedFileType;
import org.example.projectmanagementapi.repository.AttachmentRepository;
import org.example.projectmanagementapi.repository.IssueRepository;
import org.example.projectmanagementapi.service.impl.AttachmentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class AttachmentServiceTest {

  @Mock private AttachmentRepository attachmentRepository;
  @Mock private IssueRepository issueRepository;
  @Mock private MultipartFile file;

  @InjectMocks private AttachmentServiceImpl attachmentService;

  private Attachment attachment;
  private Issue issue;

  @BeforeEach
  void setUp() {
    issue = new Issue();
    issue.setId(1);
    attachment = new Attachment();
    attachment.setId(1);
    attachment.setFileType(AcceptedFileType.JPEG.name());
    attachment.setFileName("Test File");

    issue.addAttachment(attachment);
  }

  @Test
  void createAttachmentForIssue_createsAndReturnsAttachment() {
    when(issueRepository.findById(1)).thenReturn(Optional.of(issue));
    when(attachmentRepository.save(any(Attachment.class))).thenReturn(attachment);

    AttachmentDto createdAttachment = attachmentService.createAttachmentForIssue(file, 1);

    assertNotNull(createdAttachment);
    assertEquals("Test File", createdAttachment.getFileName());
    verify(attachmentRepository, times(1)).save(any(Attachment.class));
  }

  @Test
  void createAttachmentForIssue_throwsExceptionWhenIssueNotFound() {
    doNothing().when(attachmentRepository).findById(1);


    assertThrows(RuntimeException.class, () -> attachmentService.createAttachmentForIssue(file, 1));
    verify(attachmentRepository, never()).save(any(Attachment.class));
  }


  @Test
  void deleteAttachment_deletesAttachment() {
    when(attachmentRepository.findById(1)).thenReturn(Optional.of(attachment));

    attachmentService.deleteAttachment(1);

    verify(attachmentRepository, times(1)).delete(attachment);
  }

  @Test
  void deleteAttachment_throwsExceptionWhenNotFound() {
    when(attachmentRepository.findById(1)).thenReturn(Optional.empty());

    assertThrows(RuntimeException.class, () -> attachmentService.deleteAttachment(1));
    verify(attachmentRepository, never()).delete(any(Attachment.class));
  }
}