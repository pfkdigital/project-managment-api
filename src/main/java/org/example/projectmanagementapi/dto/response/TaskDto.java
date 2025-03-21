package org.example.projectmanagementapi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.projectmanagementapi.enums.PriorityStatus;
import org.example.projectmanagementapi.enums.TaskStatus;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TaskDto {
  private Integer id;
  private String description;
  private LocalDate dueDate;
  private PriorityStatus priority;
  private TaskStatus status;
  private NestedProjectDto project;
  private List<UserDto> users;
}
