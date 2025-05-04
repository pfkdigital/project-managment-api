package org.example.projectmanagementapi.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CommentRequestDto implements Serializable {
  @NotNull(message = "Comment must have content")
  @Length(min = 1, max = 255, message = "Comment content must be between 1 and 255 characters")
  String content;

  Integer taskId;
  Integer issueId;

  @NotNull(message = "Comment must have an author")
  Integer authorId;
}
