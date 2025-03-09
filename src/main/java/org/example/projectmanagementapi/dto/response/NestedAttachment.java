package org.example.projectmanagementapi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class NestedAttachment {
    private Integer id;
    private String fileName;
    private String filePath;
    private String fileType;
    private LocalDate uploadedAt;
}
