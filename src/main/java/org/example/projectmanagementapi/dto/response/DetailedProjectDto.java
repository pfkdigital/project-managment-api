package org.example.projectmanagementapi.dto.response;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.projectmanagementapi.enums.ProjectStatus;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class DetailedProjectDto implements Serializable {
    private Integer id;
    private String name;
    private String description;
    private ProjectStatus status;
    private UserDto owner;
    private String displayImageUrl;
    private List<UserDto> collaborators;
    private List<TaskDto> tasks;
    private List<IssueDto> issues;
}