package org.example.projectmanagementapi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.projectmanagementapi.enums.IssueStatus;
import org.example.projectmanagementapi.enums.PriorityStatus;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class DetailedIssueDto {
    private Integer id;
    private String title;
    private String description;
    private IssueStatus status;
    private PriorityStatus priorityStatus;
    private UserDto reportedBy;
    private UserDto assignedTo;
    private List<CommentDto> comments;
    private List<NestedAttachment> attachments;
}
