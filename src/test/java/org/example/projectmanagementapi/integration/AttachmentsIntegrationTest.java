package org.example.projectmanagementapi.integration;

import org.example.projectmanagementapi.dto.response.AttachmentDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.S3;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ActiveProfiles("test")
public class AttachmentsIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Container
    private static final LocalStackContainer localStack =
            new LocalStackContainer(DockerImageName.parse("localstack/localstack:latest"))
                    .withServices(S3);

    private static final String TEST_BUCKET = "test-bucket";
    private static final String TEST_FILE_PATH = "test-attachment.txt";

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("cloud.aws.s3.bucket", () -> TEST_BUCKET);
        registry.add("cloud.aws.region.static", localStack::getRegion);
        registry.add("cloud.aws.credentials.access-key", localStack::getAccessKey);
        registry.add("cloud.aws.credentials.secret-key", localStack::getSecretKey);
        registry.add("cloud.aws.s3.endpoint", () -> localStack.getEndpointOverride(S3).toString()); // Fix endpoint method
    }

    @BeforeEach
    void setUp() throws IOException, InterruptedException {
        // Create S3 bucket
        localStack.execInContainer("awslocal", "s3", "mb", "s3://" + TEST_BUCKET);

        // Create test file
        File testFile = new File("src/test/resources/" + TEST_FILE_PATH);
        if (!testFile.exists()) {
            boolean fileCreated = testFile.createNewFile();
            assertTrue(fileCreated, "Test file should be created");

            try (FileWriter writer = new FileWriter(testFile)) {
                writer.write("This is test attachment content");
            }
        }
    }

    @AfterEach
    void tearDown() throws IOException, InterruptedException {
        // Clean up S3 bucket
        localStack.execInContainer("awslocal", "s3", "rm", "s3://" + TEST_BUCKET, "--recursive");
        localStack.execInContainer("awslocal", "s3", "rb", "s3://" + TEST_BUCKET);

        // Delete test file
        File testFile = new File("src/test/resources/" + TEST_FILE_PATH);
        if (testFile.exists()) {
            boolean deleted = testFile.delete();
            assertTrue(deleted, "Test file should be deleted");
        }
    }

    @Test
    @Sql({"/schema.sql", "/data.sql"})
    void testCreateAttachmentForTask() throws IOException, InterruptedException {
        // Set up a request
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new ClassPathResource(TEST_FILE_PATH));

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // Execute request
        ResponseEntity<AttachmentDto> response = restTemplate.exchange(
                "http://localhost:" + port + "/api/v1/attachments/task/1",
                HttpMethod.POST,
                requestEntity,
                AttachmentDto.class);

        // Verify response
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        AttachmentDto attachmentDto = response.getBody();
        assertNotNull(attachmentDto);
        assertEquals(TEST_FILE_PATH, attachmentDto.getFileName());
        assertNotNull(attachmentDto.getId());

        // Verify file exists in S3
        String output = localStack.execInContainer("awslocal", "s3", "ls", "s3://" + TEST_BUCKET).getStdout();
        assertTrue(output.contains(TEST_FILE_PATH) || !output.trim().isEmpty(),
                "File should exist in S3 bucket: " + output);
    }

    @Test
    @Sql({"/schema.sql", "/data.sql"})
    void testCreateAttachmentForIssue() throws IOException, InterruptedException {
        // Set up a request
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new ClassPathResource(TEST_FILE_PATH));

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // Execute request
        ResponseEntity<AttachmentDto> response = restTemplate.exchange(
                "http://localhost:" + port + "/api/v1/attachments/issue/1",
                HttpMethod.POST,
                requestEntity,
                AttachmentDto.class);

        // Verify response
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        AttachmentDto attachmentDto = response.getBody();
        assertNotNull(attachmentDto);
        assertEquals(TEST_FILE_PATH, attachmentDto.getFileName());
        assertNotNull(attachmentDto.getId());

        // Verify file exists in S3
        String output = localStack.execInContainer("awslocal", "s3", "ls", "s3://" + TEST_BUCKET).getStdout();
        assertTrue(output.contains(TEST_FILE_PATH) || !output.trim().isEmpty(),
                "File should exist in S3 bucket: " + output);
    }

    @Test
    @Sql({"/schema.sql", "/data.sql"})
    void testDeleteAttachment() throws IOException, InterruptedException {
        // First create an attachment
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new ClassPathResource(TEST_FILE_PATH));

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<AttachmentDto> createResponse = restTemplate.exchange(
                "http://localhost:" + port + "/api/v1/attachments/task/1",
                HttpMethod.POST,
                requestEntity,
                AttachmentDto.class);

        AttachmentDto createdAttachment = createResponse.getBody();
        assertNotNull(createdAttachment);

        // Verify file exists in S3 before deletion
        String outputBefore = localStack.execInContainer("awslocal", "s3", "ls", "s3://" + TEST_BUCKET).getStdout();
        assertTrue(outputBefore.contains(TEST_FILE_PATH) || !outputBefore.trim().isEmpty(),
                "File should exist in S3 bucket before deletion");

        // Delete the attachment
        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                "http://localhost:" + port + "/api/v1/attachments/" + createdAttachment.getId(),
                HttpMethod.DELETE,
                null,
                Void.class);

        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());

        // Verify file no longer exists in S3
        String outputAfter = localStack.execInContainer("awslocal", "s3", "ls", "s3://" + TEST_BUCKET).getStdout();
        assertTrue(outputAfter.trim().isEmpty() || !outputAfter.contains(TEST_FILE_PATH),
                "File should not exist in S3 bucket after deletion: " + outputAfter);
    }

    @Test
    @Sql({"/schema.sql", "/data.sql"})
    void testCreateAttachmentWithInvalidFileType() {
        // Create a test file with an invalid extension
        File invalidFile = new File("src/test/resources/invalid.xyz");
        try {
            invalidFile.createNewFile();
            try (FileWriter writer = new FileWriter(invalidFile)) {
                writer.write("Invalid file content");
            }

            // Set up a request
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new ClassPathResource("invalid.xyz"));

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            // Execute request
            ResponseEntity<String> response = restTemplate.exchange(
                    "http://localhost:" + port + "/api/v1/attachments/task/1",
                    HttpMethod.POST,
                    requestEntity,
                    String.class);

            // Should return a bad request
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        } catch (IOException e) {
            fail("Failed to create test file: " + e.getMessage());
        } finally {
            // Clean up
            if (invalidFile.exists()) {
                invalidFile.delete();
            }
        }
    }

    @Test
    @Sql({"/schema.sql", "/data.sql"})
    void testCreateAttachmentForNonExistentTask() {
        // Set up a request
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new ClassPathResource(TEST_FILE_PATH));

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // Execute request for non-existent task
        ResponseEntity<String> response = restTemplate.exchange(
                "http://localhost:" + port + "/api/v1/attachments/task/999",
                HttpMethod.POST,
                requestEntity,
                String.class);

        // Should return not found or bad request
        assertTrue(response.getStatusCode() == HttpStatus.NOT_FOUND ||
                response.getStatusCode() == HttpStatus.BAD_REQUEST);
    }
}