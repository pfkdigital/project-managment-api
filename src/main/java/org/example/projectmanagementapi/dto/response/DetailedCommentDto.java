package org.example.projectmanagementapi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class DetailedCommentDto {
    private Integer id;
    private String content;
    private LocalDate createdAt;
    private LocalDate updatedAt;
    private boolean isEdited;
    private UserDto author;
    private NestedProjectDto project;
}
