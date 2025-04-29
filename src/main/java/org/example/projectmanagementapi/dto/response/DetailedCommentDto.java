package org.example.projectmanagementapi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class DetailedCommentDto implements Serializable {
    private Integer id;
    private String content;
    private LocalDate createdAt;
    private LocalDate updatedAt;
    private boolean isEdited;
    private UserDto author;
}
