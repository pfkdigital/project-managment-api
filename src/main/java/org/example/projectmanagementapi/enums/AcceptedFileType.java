package org.example.projectmanagementapi.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.MediaType;

@Getter
@AllArgsConstructor
public enum AcceptedFileType {
    JPG("jpg", MediaType.IMAGE_JPEG),
    JPEG("jpeg", MediaType.IMAGE_JPEG),
    TXT("txt", MediaType.TEXT_PLAIN),
    PNG("png", MediaType.IMAGE_PNG),
    PDF("pdf", MediaType.APPLICATION_PDF);

    private final String fileType;
    private final MediaType mediaType;
}
