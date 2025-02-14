package org.example.projectmanagementapi.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Value;
import org.example.projectmanagementapi.entity.Task;
import org.example.projectmanagementapi.enums.PriorityStatus;
import org.example.projectmanagementapi.enums.TaskStatus;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO for {@link Task}
 */
@Value
public class TaskDto implements Serializable {
    @NotNull(message = "Task must have a description")
    String description;
    @NotNull(message = "Task must have a due date")
    @Future(message = "Due date must not be in the past")
    LocalDate dueDate;
    @NotNull(message = "Task must have a priority status")
    PriorityStatus priority;
    @NotNull(message = "Task must have a status")
    @Enumerated(EnumType.STRING)
    TaskStatus status;
    @NotNull
    @Size(min = 1)
    List<UserDto> users;
    @NotNull(message = "Task must be associated with a project")
    Integer projectId;
}