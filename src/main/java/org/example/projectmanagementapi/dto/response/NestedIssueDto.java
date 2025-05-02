package org.example.projectmanagementapi.dto.response;

import lombok.*;
import org.example.projectmanagementapi.enums.IssueStatus;
import org.example.projectmanagementapi.enums.PriorityStatus;

import java.io.Serializable;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class NestedIssueDto implements Serializable {
    Integer id;
    String title;
    String description;
    IssueStatus status;
    PriorityStatus priorityStatus;
    UserDto reportedBy;
    UserDto assignedTo;
}