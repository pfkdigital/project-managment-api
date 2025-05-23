package org.example.projectmanagementapi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.projectmanagementapi.enums.NotificationType;

import java.io.Serializable;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class NotificationDto implements Serializable {
    private Integer id;
    private String message;
    private NotificationType type;
    private Boolean isRead;
    private String createdAt;
}
