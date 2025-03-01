package org.example.projectmanagementapi.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

import org.example.projectmanagementapi.dto.response.AttachmentDto;
import org.example.projectmanagementapi.service.impl.AttachmentServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
public class AttachmentControllerTest {

  @Mock private AttachmentServiceImpl attachmentService;

  @InjectMocks private AttachmentController attachmentController;

  @Mock private MultipartFile file;

  @Test
  void testCreateAttachment() {
    AttachmentDto attachmentDto = new AttachmentDto();
    when(attachmentService.createAttachmentForIssue(any(MultipartFile.class), anyInt())).thenReturn(attachmentDto);

    ResponseEntity<?> response = attachmentController.createAttachmentForIssue(file, 1);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(attachmentDto, response.getBody());
  }

  @Test
  void testDeleteAttachment() {
    doNothing().when(attachmentService).deleteAttachment(anyInt());

    ResponseEntity<?> response = attachmentController.deleteAttachment(1);

    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    verify(attachmentService, times(1)).deleteAttachment(1);
  }
}