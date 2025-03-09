package org.example.projectmanagementapi.dto.request;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import lombok.*;
import org.example.projectmanagementapi.dto.response.UserDto;
import org.example.projectmanagementapi.enums.PriorityStatus;
import org.example.projectmanagementapi.enums.TaskStatus;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TaskRequestDto implements Serializable {
  @NotNull(message = "Task must have a description")
  private String description;

  @NotNull(message = "Task must have a due date")
  @Future(message = "Due date must not be in the past")
  private LocalDate dueDate;

  @NotNull(message = "Task must have a priority status")
  private PriorityStatus priority;

  @NotNull(message = "Task must have a status")
  @Enumerated(EnumType.STRING)
  private TaskStatus status;

  @NotNull(message = "Task must be associated with a project")
  private Integer projectId;
}
