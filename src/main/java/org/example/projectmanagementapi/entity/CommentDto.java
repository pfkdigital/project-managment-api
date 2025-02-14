package org.example.projectmanagementapi.entity;

import jakarta.validation.constraints.NotNull;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link Comment}
 */
@Value
public class CommentDto implements Serializable {
    @NotNull(message = "Comment must have content")
    String content;
    Integer taskId;
    Integer issueId;
    @NotNull(message = "Comment must have an author")
    Integer authorId;
}