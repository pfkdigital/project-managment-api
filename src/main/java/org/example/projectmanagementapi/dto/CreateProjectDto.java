package org.example.projectmanagementapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Value;
import org.example.projectmanagementapi.entity.User;
import org.hibernate.validator.constraints.URL;

import java.io.Serializable;

public record CreateProjectDto(
        @NotNull(message = "Project must have a name") @NotEmpty(message = "Project must have a name") @NotBlank(message = "Project must have a name") String name,
        @NotNull(message = "Project must have a description") @NotEmpty(message = "Project must have a description") @NotBlank(message = "Project must have a description") String description,
        @NotNull(message = "Project must have a description") @NotEmpty(message = "Project must have a description") @NotBlank(message = "Project must have a description") User owner,
        @NotNull(message = "Project must have a displayImageUrl") @NotEmpty @URL String displayImageUrl) implements Serializable {
}