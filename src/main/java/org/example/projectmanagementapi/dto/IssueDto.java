package org.example.projectmanagementapi.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.*;
import org.example.projectmanagementapi.enums.IssueStatus;
import org.example.projectmanagementapi.enums.PriorityStatus;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode
public class IssueDto implements Serializable {
  @NotNull(message = "Issue must have a title")
  private String title;

  @NotNull(message = "Issue must have a description")
  private String description;

  @NotNull(message = "Issue must have a status")
  private IssueStatus status;

  @NotNull(message = "Issue must have a priority status")
  @Enumerated(EnumType.STRING)
  private PriorityStatus priorityStatus;

  @NotNull(message = "Issue must have a project id")
  private Integer projectId;

  @NotNull(message = "Issue must have a reported by id")
  private Integer reportedById;

  @NotNull(message = "Issue must have a status")
  private Integer assignedToId;
}
