package org.example.projectmanagementapi.dto.response;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CommentDto implements Serializable {
  Integer id;
  String content;
  LocalDate createdAt;
  LocalDate updatedAt;
  Integer taskId;
  Integer issueId;
  UserDto author;
}
