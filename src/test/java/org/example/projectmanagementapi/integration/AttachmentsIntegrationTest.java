package org.example.projectmanagementapi.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.example.projectmanagementapi.dto.response.AttachmentDto;
import org.example.projectmanagementapi.service.AttachmentService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.mock.web.MockMultipartFile;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.FileInputStream;
import java.io.IOException;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service.S3;

@Testcontainers
@SpringBootTest
public class AttachmentsIntegrationTest {

    @Container
    private static final LocalStackContainer localStack =
            new LocalStackContainer(DockerImageName.parse("localstack/localstack:latest"))
                    .withServices(S3);

    @Autowired
    private AttachmentService attachmentService;

    @Autowired
    private S3Client amazonS3;

    private static String TEST_BUCKET = "test-bucket";


    @DynamicPropertySource
    static void configureProperties(org.springframework.test.context.DynamicPropertyRegistry registry) {
        registry.add("cloud.aws.s3.bucket", () -> TEST_BUCKET );
        registry.add("cloud.aws.region.static", localStack::getRegion);
    }

    @BeforeEach
    void setUp() {
        amazonS3.createBucket(builder -> builder.bucket(TEST_BUCKET));
    }

    @AfterEach
    void tearDown() {
        amazonS3.listObjects(builder -> builder.bucket(TEST_BUCKET)).contents().forEach(s3Object -> amazonS3.deleteObject(builder -> builder.bucket(TEST_BUCKET).key(s3Object.key())));
        amazonS3.deleteBucket(builder -> builder.bucket(TEST_BUCKET));
    }

    @Test
    void testCreateAttachmentForTask() throws IOException {
        // Prepare a mock file
        MultipartFile file = new MockMultipartFile(
                "file",
                "test-file.txt",
                "text/plain",
                new FileInputStream("src/test/resources/test-file.txt")
        );

        // Call the service method
        AttachmentDto attachmentDto = attachmentService.createAttachmentForTask(file, 1);

        // Assertions
        assertNotNull(attachmentDto);
        assertEquals("test-file.txt", attachmentDto.getFileName());

        // Verify the file exists in S3
        assertEquals(1, amazonS3.listObjects(builder -> builder.bucket(TEST_BUCKET)).contents().size());
    }

    @Test
    void testCreateAttachmentForIssue() throws IOException {
        // Prepare a mock file
        MultipartFile file = new MockMultipartFile(
                "file",
                "test-file.txt",
                "text/plain",
                new FileInputStream("src/test/resources/test-file.txt")
        );

        // Call the service method
        AttachmentDto attachmentDto = attachmentService.createAttachmentForIssue(file, 1);

        // Assertions
        assertNotNull(attachmentDto);
        assertEquals("test-file.txt", attachmentDto.getFileName());

        // Verify the file exists in S3
        assertEquals(1, amazonS3.listObjects(builder -> builder.bucket(TEST_BUCKET)).contents().size());
    }

    @Test
    void testDeleteAttachment() {
        // Simulate an attachment creation
        AttachmentDto attachmentDto = attachmentService.createAttachmentForTask(
                new MockMultipartFile("file", "test-file.txt", "text/plain", "content".getBytes()), 1);

        // Delete the attachment
        attachmentService.deleteAttachment(attachmentDto.getId());

        // Verify the file no longer exists in S3
        assertEquals(0, amazonS3.listObjects(builder -> builder.bucket(TEST_BUCKET)).contents().size());
    }
}