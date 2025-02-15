package org.example.projectmanagementapi.controller;

import org.example.projectmanagementapi.entity.Attachment;
import org.example.projectmanagementapi.service.AttachmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

public class AttachmentControllerTest {

    @Mock
    private AttachmentService attachmentService;

    @InjectMocks
    private AttachmentController attachmentController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateAttachmentForTask() {
        Attachment attachment = Attachment.builder().id(1).fileName("file.txt").build();
        when(attachmentService.createAttachmentForTask(any(MultipartFile.class), anyInt())).thenReturn(attachment);

        ResponseEntity<?> response = attachmentController.createAttachmentForTask(mock(MultipartFile.class), 1);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void testCreateAttachmentForIssue() {
        Attachment attachment = Attachment.builder().id(1).fileName("file.txt").build();
        when(attachmentService.createAttachmentForIssue(any(MultipartFile.class), anyInt())).thenReturn(attachment);

        ResponseEntity<?> response = attachmentController.createAttachmentForIssue(mock(MultipartFile.class), 1);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void testDeleteAttachment() {
        doNothing().when(attachmentService).deleteAttachment(anyInt());

        ResponseEntity<?> response = attachmentController.deleteAttachment(1);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(attachmentService, times(1)).deleteAttachment(1);
    }
}