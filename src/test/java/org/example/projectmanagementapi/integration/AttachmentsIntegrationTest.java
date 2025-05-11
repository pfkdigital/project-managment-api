package org.example.projectmanagementapi.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.projectmanagementapi.config.TestJpaConfig;
import org.example.projectmanagementapi.dto.response.AttachmentDto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest(
        properties = {
                "spring.main.allow-bean-definition-overriding=true",
                "spring.servlet.multipart.max-file-size=10MB",
                "spring.servlet.multipart.max-request-size=10MB"
        })
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestJpaConfig.class)
@DisplayName("Attachment API Integration Tests")
public class AttachmentsIntegrationTest {

  @Container
  public static LocalStackContainer localStack =
          new LocalStackContainer(DockerImageName.parse("localstack/localstack:latest"))
                  .withServices(LocalStackContainer.Service.S3);

  private static final String BUCKET_NAME = "pfk-task-attachments";
  private static final String REGION = "us-east-1";
  private static S3Client s3Client;

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @DynamicPropertySource
  static void registerS3Properties(DynamicPropertyRegistry registry) {
    registry.add("cloud.aws.s3.bucket", () -> BUCKET_NAME);
    registry.add("cloud.aws.endpoint", () -> localStack.getEndpointOverride(LocalStackContainer.Service.S3).toString());
    registry.add("cloud.aws.region.static", () -> REGION);
    registry.add("cloud.aws.credentials.access-key", () -> localStack.getAccessKey());
    registry.add("cloud.aws.credentials.secret-key", () -> localStack.getSecretKey());
    registry.add("cloud.aws.stack.auto", () -> false);
    registry.add("cloud.aws.stack.enabled", () -> false);
  }

  @BeforeAll
  static void setup() {
    // Configure S3 client to use LocalStack
    s3Client = S3Client.builder()
            .endpointOverride(localStack.getEndpointOverride(LocalStackContainer.Service.S3))
            .region(Region.of(REGION))
            .credentialsProvider(
                    StaticCredentialsProvider.create(
                            AwsBasicCredentials.create(localStack.getAccessKey(), localStack.getSecretKey())))
            .forcePathStyle(true)
            .build();

    // Create the test bucket
    try {
      s3Client.createBucket(CreateBucketRequest.builder().bucket(BUCKET_NAME).build());
    } catch (BucketAlreadyExistsException e) {
      // Bucket already exists, which is fine
    }
  }

  private String getBaseUrl() {
    return "/api/v1/attachments";
  }

  private String extractS3KeyFromUrl(String url) {
    if (url.contains(".amazonaws.com/")) {
      return url.substring(url.indexOf(".amazonaws.com/") + ".amazonaws.com/".length());
    }
    return url;
  }

  @Nested
  @DisplayName("Attachment Upload Operations")
  class AttachmentUploadTests {

    @Test
    @WithMockUser(roles = "USER")
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Upload attachment to task and verify in S3")
    public void testUploadAttachmentToTaskAndVerifyInS3() throws Exception {
      // Create test content
      String fileContent = "This is a test file content for task attachment";
      String fileName = "s3-verify-task.txt";

      MockMultipartFile file = new MockMultipartFile(
              "file",
              fileName,
              MediaType.TEXT_PLAIN_VALUE,
              fileContent.getBytes()
      );

      MvcResult result = mockMvc
              .perform(multipart(getBaseUrl() + "/task/1")
                      .file(file)
                      .contentType(MediaType.MULTIPART_FORM_DATA))
              .andExpect(status().isCreated())
              .andReturn();

      AttachmentDto attachment = objectMapper.readValue(
              result.getResponse().getContentAsString(), AttachmentDto.class);

      assertNotNull(attachment);
      assertNotNull(attachment.getId());
      assertEquals(fileName, attachment.getFileName());

      // Extract S3 key from the URL format
      String s3Key = extractS3KeyFromUrl(attachment.getFilePath());

      // Verify file exists in S3
      try {
        HeadObjectResponse headObjectResponse = s3Client.headObject(
                HeadObjectRequest.builder()
                        .bucket(BUCKET_NAME)
                        .key(s3Key)
                        .build()
        );
        assertNotNull(headObjectResponse);

        // Verify content matches
        ResponseInputStream<GetObjectResponse> response = s3Client.getObject(
                GetObjectRequest.builder()
                        .bucket(BUCKET_NAME)
                        .key(s3Key)
                        .build()
        );

        byte[] bytes = response.readAllBytes();
        String retrievedContent = new String(bytes, StandardCharsets.UTF_8);
        assertEquals(fileContent, retrievedContent);
      } catch (NoSuchKeyException e) {
        fail("File should exist in S3 at key: " + s3Key);
      }
    }

    @Test
    @WithMockUser(roles = "USER")
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Upload attachment to issue and verify in S3")
    public void testUploadAttachmentToIssueAndVerifyInS3() throws Exception {
      String fileContent = "This is a test file content for issue attachment";
      String fileName = "s3-verify-issue.txt";

      MockMultipartFile file = new MockMultipartFile(
              "file",
              fileName,
              MediaType.TEXT_PLAIN_VALUE,
              fileContent.getBytes()
      );

      MvcResult result = mockMvc
              .perform(multipart(getBaseUrl() + "/issue/1")
                      .file(file)
                      .contentType(MediaType.MULTIPART_FORM_DATA))
              .andExpect(status().isCreated())
              .andReturn();

      AttachmentDto attachment = objectMapper.readValue(
              result.getResponse().getContentAsString(), AttachmentDto.class);

      assertNotNull(attachment);
      assertNotNull(attachment.getId());
      assertEquals(fileName, attachment.getFileName());

      // Extract the S3 key from the file path
      String s3Key = extractS3KeyFromUrl(attachment.getFilePath());

      // Expected format: attachments/issues/1/s3-verify-issue.txt
      assertTrue(s3Key.contains("attachments/issues/1/"),
              "S3 key should contain the expected path structure");

      // Verify the object exists in S3
      boolean objectExists = s3ObjectExists(BUCKET_NAME, s3Key);
      assertTrue(objectExists, "Object should exist in S3 bucket at key: " + s3Key);

      // Retrieve and verify content
      ResponseInputStream<GetObjectResponse> response = s3Client.getObject(
              GetObjectRequest.builder()
                      .bucket(BUCKET_NAME)
                      .key(s3Key)
                      .build()
      );

      byte[] bytes = response.readAllBytes();
      String retrievedContent = new String(bytes, StandardCharsets.UTF_8);
      assertEquals(fileContent, retrievedContent);
    }
  }

  @Nested
  @DisplayName("Attachment Delete Operations")
  class AttachmentDeleteTests {

    @Test
    @WithMockUser(roles = "USER")
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Delete existing attachment and verify removal from S3")
    public void testDeleteAttachmentAndVerifyS3() throws Exception {
      // First upload an attachment
      String fileContent = "File to be deleted";
      String fileName = "to-delete.txt";

      MockMultipartFile file = new MockMultipartFile(
              "file", fileName, MediaType.TEXT_PLAIN_VALUE, fileContent.getBytes());

      MvcResult result = mockMvc
              .perform(multipart(getBaseUrl() + "/task/1")
                      .file(file)
                      .contentType(MediaType.MULTIPART_FORM_DATA))
              .andExpect(status().isCreated())
              .andReturn();

      AttachmentDto attachment = objectMapper.readValue(
              result.getResponse().getContentAsString(), AttachmentDto.class);

      String s3Key = extractS3KeyFromUrl(attachment.getFilePath());

      // Verify it exists in S3 before deletion
      assertTrue(s3ObjectExists(BUCKET_NAME, s3Key),
              "File should exist before deletion at key: " + s3Key);

      // Delete it through the API
      mockMvc.perform(delete(getBaseUrl() + "/" + attachment.getId()))
              .andExpect(status().isNoContent());

      // Verify it's been removed from S3
      assertFalse(s3ObjectExists(BUCKET_NAME, s3Key),
              "Object should not exist in S3 after deletion");
    }
  }

  @Nested
  @DisplayName("S3 Bucket Operations")
  class S3BucketTests {

    @Test
    @DisplayName("Verify S3 bucket exists")
    public void testS3BucketExists() {
      ListBucketsResponse listBucketsResponse = s3Client.listBuckets();
      boolean bucketExists = listBucketsResponse.buckets().stream()
              .anyMatch(bucket -> bucket.name().equals(BUCKET_NAME));

      assertTrue(bucketExists, "Test bucket should exist in S3");
    }

    @Test
    @DisplayName("Test direct S3 operations")
    public void testDirectS3Operations() throws IOException {
      // Create a test key
      String testKey = "attachments/direct-test/test-file.txt";
      String content = "Test content for direct S3 operations";

      // Upload directly to S3
      s3Client.putObject(
              PutObjectRequest.builder()
                      .bucket(BUCKET_NAME)
                      .key(testKey)
                      .build(),
              RequestBody.fromString(content)
      );

      ResponseInputStream<GetObjectResponse> response = s3Client.getObject(
              GetObjectRequest.builder()
                      .bucket(BUCKET_NAME)
                      .key(testKey)
                      .build()
      );

      byte[] bytes = response.readAllBytes();
      String retrievedContent = new String(bytes, StandardCharsets.UTF_8);

      assertEquals(content, retrievedContent, "Content retrieved from S3 should match original");

      // Delete the object
      s3Client.deleteObject(
              DeleteObjectRequest.builder()
                      .bucket(BUCKET_NAME)
                      .key(testKey)
                      .build()
      );

      // Verify it's deleted
      assertFalse(s3ObjectExists(BUCKET_NAME, testKey),
              "Object should be deleted from S3");
    }
  }

  @Nested
  @DisplayName("Attachment Error Cases")
  class AttachmentErrorTests {

    @Test
    @WithMockUser(roles = "USER")
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Upload to non-existent task")
    public void testUploadToNonExistentTask() throws Exception {
      MockMultipartFile file = new MockMultipartFile(
              "file", "test.txt", MediaType.TEXT_PLAIN_VALUE, "test content".getBytes());

      mockMvc.perform(multipart(getBaseUrl() + "/task/999")
                      .file(file)
                      .contentType(MediaType.MULTIPART_FORM_DATA))
              .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Upload oversized file")
    public void testUploadOversizedFile() throws Exception {
      // Create a file that exceeds the limit (10MB)
      byte[] largeContent = new byte[10 * 1024 * 1024 + 1]; // Just over 10MB
      MockMultipartFile largeFile =
              new MockMultipartFile("file", "large.txt", MediaType.TEXT_PLAIN_VALUE, largeContent);

      mockMvc.perform(multipart(getBaseUrl() + "/task/1")
                      .file(largeFile)
                      .contentType(MediaType.MULTIPART_FORM_DATA))
              .andExpect(status().isPayloadTooLarge());
    }
  }

  private boolean s3ObjectExists(String bucket, String key) {
    try {
      s3Client.headObject(HeadObjectRequest.builder().bucket(bucket).key(key).build());
      return true;
    } catch (NoSuchKeyException e) {
      return false;
    }
  }
}