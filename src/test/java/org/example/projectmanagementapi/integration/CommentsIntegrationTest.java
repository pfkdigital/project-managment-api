package org.example.projectmanagementapi.integration;

import org.example.projectmanagementapi.dto.request.CommentRequestDto;
import org.example.projectmanagementapi.dto.request.CommentUpdateRequest;
import org.example.projectmanagementapi.dto.response.CommentDto;
import org.example.projectmanagementapi.exception.ApiError;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.jdbc.Sql;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CommentsIntegrationTest extends BaseIntegration {
  @Autowired private TestRestTemplate restTemplate;

  @LocalServerPort private int port;

  @Test
  @Sql({"/schema.sql", "/data.sql"})
  public void testCreateComment() {
    String url = "http://localhost:" + port + "/api/v1/comments";

    CommentRequestDto commentRequestDto = new CommentRequestDto();
    commentRequestDto.setContent("Test Comment");
    commentRequestDto.setIssueId(1);
    commentRequestDto.setAuthorId(1);

    CommentDto response = restTemplate.postForObject(url, commentRequestDto, CommentDto.class);

    assertNotNull(response);
    assertEquals("Test Comment", response.getContent());
  }

  @Test
  @Sql({"/schema.sql", "/data.sql"})
  public void testGetAllComments() {
    String url = "http://localhost:" + port + "/api/v1/comments";
    CommentDto[] comments = restTemplate.getForObject(url, CommentDto[].class);

    assertNotNull(comments);
    assertEquals(5, comments.length);
    assertEquals("This task is almost done.", comments[0].getContent());
  }

  @Test
  @Sql({"/schema.sql", "/data.sql"})
  public void testGetCommentById() {
    String url = "http://localhost:" + port + "/api/v1/comments/1";
    CommentDto comment = restTemplate.getForObject(url, CommentDto.class);

    assertNotNull(comment);
    assertEquals("This task is almost done.", comment.getContent());
  }

  @Test
  @Sql({"/schema.sql", "/data.sql"})
  public void testGetCommentByIdNotFound() {
    String url = "http://localhost:" + port + "/api/v1/comments/999";
    ApiError errorResponse = restTemplate.getForObject(url, ApiError.class);

    assertNotNull(errorResponse);
    assertEquals("Comment with id 999 not found", errorResponse.getMessage());
  }

  @Test
  @Sql({"/schema.sql", "/data.sql"})
  public void testUpdateComment() {
    String url = "http://localhost:" + port + "/api/v1/comments/1";

    CommentUpdateRequest commentRequestDto = new CommentUpdateRequest();
    commentRequestDto.setContent("Updated Comment");
    commentRequestDto.setAuthorId(2);

    restTemplate.put(url, commentRequestDto, CommentUpdateRequest.class);

    CommentDto response = restTemplate.getForObject(url, CommentDto.class);

    assertNotNull(response);
    assertEquals("Updated Comment", response.getContent());
  }

  @Test
  @Sql({"/schema.sql", "/data.sql"})
  public void testDeleteComment() {
    String url = "http://localhost:" + port + "/api/v1/comments/1";

    restTemplate.delete(url);

    ApiError errorResponse = restTemplate.getForObject(url, ApiError.class);

    assertNotNull(errorResponse);
    assertEquals("Comment with id 1 not found", errorResponse.getMessage());
  }
}
