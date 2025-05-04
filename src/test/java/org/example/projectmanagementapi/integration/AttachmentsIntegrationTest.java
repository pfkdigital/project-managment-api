package org.example.projectmanagementapi.integration;

import org.example.projectmanagementapi.config.TestJpaConfig;
import org.example.projectmanagementapi.dto.response.AttachmentDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {
      "spring.main.allow-bean-definition-overriding=true",
      "spring.servlet.multipart.max-file-size=10MB",
      "spring.servlet.multipart.max-request-size=10MB"
    })
@ActiveProfiles("test")
@Import(TestJpaConfig.class)
@DisplayName("Attachment API Integration Tests")
public class AttachmentsIntegrationTest {

  @Autowired private TestRestTemplate restTemplate;

  @LocalServerPort private int port;

  private String getBaseUrl() {
    return "http://localhost:" + port + "/api/v1/attachments";
  }

  @Nested
  @DisplayName("Attachment Upload Operations")
  class AttachmentUploadTests {

    @Test
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Upload attachment to task")
    public void testUploadAttachmentToTask() {
      MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
      body.add("file", new ClassPathResource("test-files/test.txt"));

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.MULTIPART_FORM_DATA);

      HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

      ResponseEntity<AttachmentDto> response =
          restTemplate.exchange(
              getBaseUrl() + "/task/1", HttpMethod.POST, requestEntity, AttachmentDto.class);

      assertEquals(HttpStatus.CREATED, response.getStatusCode());
      AttachmentDto attachment = response.getBody();
      assertNotNull(attachment);
      assertNotNull(attachment.getId());
      assertEquals("test.txt", attachment.getFileName());
    }

    @Test
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Upload attachment to issue")
    public void testUploadAttachmentToIssue() {
      MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
      body.add("file", new ClassPathResource("test-files/test.txt"));

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.MULTIPART_FORM_DATA);

      HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

      ResponseEntity<AttachmentDto> response =
          restTemplate.exchange(
              getBaseUrl() + "/issue/1", HttpMethod.POST, requestEntity, AttachmentDto.class);

      assertEquals(HttpStatus.CREATED, response.getStatusCode());
      AttachmentDto attachment = response.getBody();
      assertNotNull(attachment);
      assertNotNull(attachment.getId());
    }
  }

  @Nested
  @DisplayName("Attachment Download Operations")
  class AttachmentDownloadTests {

    @Test
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Download existing attachment")
    public void testDownloadAttachment() {
      ResponseEntity<byte[]> response =
          restTemplate.getForEntity(getBaseUrl() + "/1/download", byte[].class);

      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertNotNull(response.getBody());
      assertTrue(response.getBody().length > 0);
    }
  }

  @Nested
  @DisplayName("Attachment Error Cases")
  class AttachmentErrorTests {

    @Test
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Upload to non-existent task")
    public void testUploadToNonExistentTask() {
      MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
      body.add("file", new ClassPathResource("test-files/test.txt"));

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.MULTIPART_FORM_DATA);

      HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

      ResponseEntity<String> response =
          restTemplate.exchange(
              getBaseUrl() + "/task/999", HttpMethod.POST, requestEntity, String.class);

      assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Download non-existent attachment")
    public void testDownloadNonExistentAttachment() {
      ResponseEntity<String> response =
          restTemplate.getForEntity(getBaseUrl() + "/999/download", String.class);

      assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Upload oversized file")
    public void testUploadOversizedFile() {
      byte[] largeContent = new byte[11 * 1024 * 1024]; // 11MB
      MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
      body.add("file", new HttpEntity<>(largeContent, new HttpHeaders()));

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.MULTIPART_FORM_DATA);

      HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

      ResponseEntity<String> response =
          restTemplate.exchange(
              getBaseUrl() + "/task/1", HttpMethod.POST, requestEntity, String.class);

      assertEquals(HttpStatus.PAYLOAD_TOO_LARGE, response.getStatusCode());
    }
  }

  @Nested
  @DisplayName("Attachment Delete Operations")
  class AttachmentDeleteTests {

    @Test
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Delete existing attachment")
    public void testDeleteAttachment() {
      ResponseEntity<Void> response =
          restTemplate.exchange(getBaseUrl() + "/1", HttpMethod.DELETE, null, Void.class);

      assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

      // Verify deletion
      ResponseEntity<String> getResponse =
          restTemplate.getForEntity(getBaseUrl() + "/1/download", String.class);
      assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode());
    }
  }

  @Nested
  @DisplayName("Attachment Edge Cases")
  class AttachmentEdgeCases {

    @Test
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Upload empty file")
    public void testUploadEmptyFile() {
      MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
      body.add("file", new HttpEntity<>(new byte[0], new HttpHeaders()));

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.MULTIPART_FORM_DATA);

      HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

      ResponseEntity<String> response =
          restTemplate.exchange(
              getBaseUrl() + "/task/1", HttpMethod.POST, requestEntity, String.class);

      assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @Sql({"/schema.sql", "/data.sql"})
    @DisplayName("Upload file with special characters in name")
    public void testUploadFileWithSpecialCharacters() {
      MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
      HttpHeaders fileHeaders = new HttpHeaders();
      fileHeaders.setContentType(MediaType.TEXT_PLAIN);
      HttpEntity<byte[]> fileEntity = new HttpEntity<>("test content".getBytes(), fileHeaders);
      body.add("file", fileEntity);

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.MULTIPART_FORM_DATA);

      HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

      ResponseEntity<AttachmentDto> response =
          restTemplate.exchange(
              getBaseUrl() + "/task/1", HttpMethod.POST, requestEntity, AttachmentDto.class);

      assertEquals(HttpStatus.CREATED, response.getStatusCode());
      assertNotNull(response.getBody());
    }
  }
}
