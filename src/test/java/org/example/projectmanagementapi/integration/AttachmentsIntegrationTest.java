package org.example.projectmanagementapi.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.example.projectmanagementapi.dto.response.AttachmentDto;
import org.example.projectmanagementapi.service.AttachmentService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.multipart.MultipartFile;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service.S3;

public class AttachmentsIntegrationTest extends BaseIntegration {

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    @Container
    private static final LocalStackContainer localStack =
            new LocalStackContainer(DockerImageName.parse("localstack/localstack:latest"))
                    .withServices(S3);

    @Autowired
    private AttachmentService attachmentService;

    private static String TEST_BUCKET = "test-bucket";


    @DynamicPropertySource
    static void configureProperties(org.springframework.test.context.DynamicPropertyRegistry registry) {
        registry.add("cloud.aws.s3.bucket", () -> TEST_BUCKET );
        registry.add("cloud.aws.region.static", localStack::getRegion);
        registry.add("cloud.aws.credentials.access-key", localStack::getAccessKey);
        registry.add("cloud.aws.credentials.secret-key", localStack::getSecretKey);
        registry.add("cloud.aws.s3.endpoint", localStack::getEndpoint);
    }

    @BeforeEach
    void setUp() throws IOException, InterruptedException {
        localStack.execInContainer("awslocal", "s3", "mb", "s3://" + TEST_BUCKET);

        File testFile = new File("src/test/resources/test-file.txt");
        if (!testFile.exists()) {
            testFile.createNewFile();
        }
    }

    @AfterEach
    void tearDown() throws IOException, InterruptedException {
        localStack.execInContainer("awslocal", "s3", "rm", "s3://" + TEST_BUCKET, "--recursive");
        localStack.execInContainer("awslocal", "s3", "rb", "s3://" + TEST_BUCKET);

        // Delete the temporary file
        File tempFile = new File("src/test/resources/test-file.txt");
        if (tempFile.exists()) {
            tempFile.delete();
        }
    }

    @Test
    @Sql({"/schema.sql", "/data.sql"})
    void testCreateAttachmentForTask() throws IOException, InterruptedException {
        // Prepare a mock file
        File file = new File("src/test/resources/test-file.txt");

        FileWriter fw = new FileWriter(file);
        fw.write("This is a test file.");

        // Create the URL for the endpoint
        String url = "http://localhost:" + port + "/api/v1/attachments/task/1";

        // Send a POST request to create the attachment
        AttachmentDto attachmentDto = restTemplate.postForObject(url, file, AttachmentDto.class);
    System.out.println(attachmentDto);
        // Assertions
        assertNotNull(attachmentDto);
        assertEquals("test-file.txt", attachmentDto.getFileName());

        // Verify the file exists in S3
        String output = localStack.execInContainer("awslocal", "s3", "ls", "s3://" + TEST_BUCKET).getStdout();
        long objectCount = output.lines().count();
        assertEquals(1, objectCount);
    }

    @Test
    void testCreateAttachmentForIssue() throws IOException, InterruptedException {
        // Prepare a mock file
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-file.txt",
                "text/plain",
                new FileInputStream("src/test/resources/test-file.txt")
        );

        // Create the URL for the endpoint
        String url = "http://localhost:" + port + "/api/v1/attachments/issue/1";

        // Send a POST request to create the attachment
        AttachmentDto attachmentDto = restTemplate.postForObject(url, file, AttachmentDto.class);

        // Assertions
        assertNotNull(attachmentDto);
        assertEquals("test-file.txt", attachmentDto.getFileName());

        // Verify the file exists in S3
        String output = localStack.execInContainer("awslocal", "s3", "ls", "s3://" + TEST_BUCKET).getStdout();
        long objectCount = output.lines().count();
        assertEquals(1, objectCount);
    }

    @Test
    void testDeleteAttachment() throws IOException, InterruptedException {
        // Simulate an attachment creation
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-file.txt",
                "text/plain",
                new FileInputStream("src/test/resources/test-file.txt")
        );
        String createUrl = "http://localhost:" + port + "/api/v1/attachments/task/1";
        AttachmentDto attachmentDto = restTemplate.postForObject(createUrl, file, AttachmentDto.class);

        // Delete the attachment
        String deleteUrl = "http://localhost:" + port + "/api/v1/attachments/" + attachmentDto.getId();
        restTemplate.delete(deleteUrl);

        // Verify the file no longer exists in S3
        String output = localStack.execInContainer("awslocal", "s3", "ls", "s3://" + TEST_BUCKET).getStdout();
        long objectCount = output.lines().count();
        assertEquals(0, objectCount);
    }
}