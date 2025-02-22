package org.example.projectmanagementapi.dto;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CommentDto implements Serializable {
  @NotNull(message = "Comment must have content")
  String content;

  Integer taskId;
  Integer issueId;

  @NotNull(message = "Comment must have an author")
  Integer authorId;
}
