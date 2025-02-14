package org.example.projectmanagementapi.service.impl;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.example.projectmanagementapi.entity.Attachment;
import org.example.projectmanagementapi.entity.Issue;
import org.example.projectmanagementapi.entity.Notification;
import org.example.projectmanagementapi.entity.Task;
import org.example.projectmanagementapi.enums.AcceptedFileType;
import org.example.projectmanagementapi.enums.NotificationType;
import org.example.projectmanagementapi.repository.AttachmentRepository;
import org.example.projectmanagementapi.repository.IssueRepository;
import org.example.projectmanagementapi.repository.TaskRepository;
import org.example.projectmanagementapi.service.AttachmentService;
import org.example.projectmanagementapi.service.NotificationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class AttachmentClassImpl implements AttachmentService {

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    private final AttachmentRepository attachmentRepository;
    private final TaskRepository taskRepository;
    private final IssueRepository issueRepository;
    private final NotificationService notificationService;
    private final AmazonS3 s3Client;

    @Override
    public Attachment createAttachmentForTask(MultipartFile file, Integer taskId) {
        return createAttachment(file, taskId, true);
    }

    @Override
    public Attachment createAttachmentForIssue(MultipartFile file, Integer issueId) {
        return createAttachment(file, issueId, false);
    }

    @Override
    public void deleteAttachment(Integer attachmentId) {
        Attachment attachment = attachmentRepository.findById(attachmentId).orElseThrow(() -> new RuntimeException("Attachment with id " + attachmentId + " not found"));

        try {
            s3Client.deleteObject(bucketName, attachment.getFilePath());
            attachmentRepository.delete(attachment);
        } catch (SdkClientException e) {
            throw new RuntimeException(e);
        }

        Notification notification = Notification.builder()
                .message("Attachment " + attachmentId + " has been deleted")
                .type(NotificationType.DESTRUCTION)
                .createdAt(LocalDate.now())
                .build();

        notificationService.createNotification(notification);
    }

    private Attachment createAttachment(MultipartFile file, Integer id, boolean isTask) {
        if (file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }

        if (!checkIfValidFileType(file)) {
            throw new RuntimeException("Invalid file type. Accepted file types are: " + Arrays.toString(AcceptedFileType.values()));
        }

        Attachment attachment = Attachment.builder()
                .fileName(file.getOriginalFilename())
                .fileType(file.getContentType())
                .build();

        if (isTask) {
            Task task = taskRepository.findById(id).orElseThrow(() -> new RuntimeException("Task with id " + id + " not found"));
            task.addAttachment(attachment);
            taskRepository.save(task);
        } else {
            Issue issue = issueRepository.findById(id).orElseThrow(() -> new RuntimeException("Issue with id " + id + " not found"));
            issue.addAttachment(attachment);
            issueRepository.save(issue);
        }

        uploadFileToS3(file, id, isTask, attachment);

        Notification notification = Notification.builder()
                .message("Attachment " + attachment.getId() + " has been created")
                .type(NotificationType.UPDATE)
                .createdAt(LocalDate.now())
                .build();
        notificationService.createNotification(notification);

        return attachmentRepository.save(attachment);
    }

    private void uploadFileToS3(MultipartFile file, Integer id, boolean isTask, Attachment attachment) {
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());

            String keyName = "attachments/" + (isTask ? "tasks/" : "issues/") + id + "/" + file.getOriginalFilename();

            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, keyName, file.getInputStream(), metadata);

            s3Client.putObject(putObjectRequest);
            attachment.setFilePath(s3Client.getUrl(bucketName, keyName).toString());
            attachment.setUploadedAt(LocalDate.now());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private boolean checkIfValidFileType(MultipartFile file) {
        for (AcceptedFileType type : AcceptedFileType.values()) {
            if (type.getFileType().equals(file.getContentType())) {
                return true;
            }
        }
        return false;
    }
}