package org.example.projectmanagementapi.dto.request;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CommentUpdateRequest implements Serializable {
  @NotNull(message = "Comment must have content")
  String content;

  @NotNull(message = "Comment must have an author")
  Integer authorId;
}
