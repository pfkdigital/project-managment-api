package org.example.projectmanagementapi.dto.response;

import lombok.*;
import org.example.projectmanagementapi.enums.IssueStatus;
import org.example.projectmanagementapi.enums.PriorityStatus;

import java.io.Serializable;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class IssueDto implements Serializable {
    private Integer id;
    private String title;
    private String description;
    private IssueStatus status;
    private PriorityStatus priorityStatus;
    private NestedProjectDto project;
    private UserDto reportedBy;
    private UserDto assignedTo;
}
