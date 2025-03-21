package org.example.projectmanagementapi.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.projectmanagementapi.enums.ProjectStatus;
import org.hibernate.validator.constraints.URL;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ProjectRequestDto implements Serializable {
  @NotEmpty(message = "Project must have a name")
  private String name;

  @NotEmpty(message = "Project must have a description")
  private String description;

  @NotNull(message = "Project must have a status")
  private ProjectStatus status;

  @NotNull(message = "Project must have an owner")
  Integer ownerId;

  @NotEmpty(message = "Project must have a display image url")
  private String displayImageUrl;
}
