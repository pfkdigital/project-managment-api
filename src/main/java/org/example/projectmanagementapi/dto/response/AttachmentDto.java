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
public class AttachmentDto {
    private Integer id;
    private String fileName;
    private String filePath;
    private String fileType;
    private LocalDate uploadedAt;
    private UserDto author;
}