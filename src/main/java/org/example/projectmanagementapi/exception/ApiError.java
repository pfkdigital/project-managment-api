package org.example.projectmanagementapi.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ApiError {
    private String message;
    private Integer status;
    private LocalDateTime timestamp;
}
