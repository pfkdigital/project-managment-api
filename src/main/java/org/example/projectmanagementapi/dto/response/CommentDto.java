package org.example.projectmanagementapi.dto.response;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CommentDto implements Serializable {
  private Integer id;
  private String content;
  private LocalDate createdAt;
  private UserDto author;
}
