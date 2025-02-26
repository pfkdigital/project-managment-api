package org.example.projectmanagementapi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class NotificationDto {
    private Integer id;
    private String message;
    private Boolean isRead;
    private String createdAt;
    private String updatedAt;
}
