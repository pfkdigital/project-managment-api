package org.example.projectmanagementapi.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Value;
import org.example.projectmanagementapi.entity.Project;
import org.example.projectmanagementapi.enums.ProjectStatus;
import org.hibernate.validator.constraints.URL;

import java.io.Serializable;

/**
 * DTO for {@link Project}
 */
public record UpdateProjectDto(@NotEmpty(message = "Project must have a name") String name,
                               @NotEmpty(message = "Project must have a description") String description,
                               @NotNull(message = "Project must have a status") ProjectStatus status,
                               @NotEmpty(message = "Project must have a display image url") @URL String displayImageUrl) implements Serializable {
}