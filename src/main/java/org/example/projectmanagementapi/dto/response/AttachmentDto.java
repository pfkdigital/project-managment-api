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
public class AttachmentDto implements Serializable {
    private Integer id;
    private String fileName;
    private String filePath;
    private String fileType;
    private LocalDate uploadedAt;
    private UserDto author;
}