package org.example.projectmanagementapi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.projectmanagementapi.enums.PriorityStatus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class DetailedIssueDto implements Serializable {
    private Integer id;
    private String title;
    private String description;
    private String status;
    private PriorityStatus priorityStatus;
    private UserDto reportedBy;
    private UserDto assignedTo;
    private List<DetailedCommentDto> comments;
    private List<AttachmentDto> attachments;
}
