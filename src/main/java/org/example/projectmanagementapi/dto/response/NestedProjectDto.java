package org.example.projectmanagementapi.dto.response;

import lombok.*;
import org.example.projectmanagementapi.enums.ProjectStatus;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class NestedProjectDto implements Serializable {
  Integer id;
  String name;
  String description;
  ProjectStatus status;
  String displayImageUrl;
}
