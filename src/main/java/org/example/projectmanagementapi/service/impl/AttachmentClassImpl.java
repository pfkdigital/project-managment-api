package org.example.projectmanagementapi.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.projectmanagementapi.entity.Attachment;
import org.example.projectmanagementapi.entity.Issue;
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
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AttachmentClassImpl implements AttachmentService {

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    private final AttachmentRepository attachmentRepository;
    private final TaskRepository taskRepository;
    private final IssueRepository issueRepository;
    private final NotificationService notificationService;
    private final S3Client s3Client;

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
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key("attachments/" + (attachment.getTask() != null ? "tasks/" : "issues/") + (attachment.getTask() != null ? attachment.getTask().getId() : attachment.getIssue().getId()) + "/" + attachment.getFileName())
                    .build();
            s3Client.deleteObject(deleteObjectRequest);
            attachmentRepository.delete(attachment);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        notificationService.createNotification("Attachment " + attachmentId + " has been deleted", NotificationType.DESTRUCTION);
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

        notificationService.createNotification("Attachment " + attachment.getId() + " has been created", NotificationType.UPDATE);

        return attachmentRepository.save(attachment);
    }

    private void uploadFileToS3(MultipartFile file, Integer id, boolean isTask, Attachment attachment) {
        try {
            RequestBody requestBody = RequestBody.fromInputStream(file.getInputStream(), file.getSize());

            String keyName = "attachments/" + (isTask ? "tasks/" : "issues/") + id + "/" + file.getOriginalFilename();

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(keyName)
                    .contentType(file.getContentType())
                    .contentLength(file.getSize())
                    .build();

            PutObjectResponse response = s3Client.putObject(putObjectRequest,requestBody);

            attachment.setFilePath("https://" + bucketName + ".s3.amazonaws.com/" + keyName);
            attachment.setUploadedAt(LocalDate.now());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private boolean checkIfValidFileType(MultipartFile file) {
        System.out.println(file.getContentType() + "=>" + file.getOriginalFilename());
        for (AcceptedFileType type : AcceptedFileType.values()) {
            if (Objects.equals(file.getContentType(), type.getMediaType().toString())) {
                return true;
            }
        }
        return false;
    }
}