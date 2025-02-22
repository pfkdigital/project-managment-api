package org.example.projectmanagementapi.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.MediaType;

@Getter
@AllArgsConstructor
public enum AcceptedFileType {
  JPG(MediaType.IMAGE_JPEG),
  JPEG(MediaType.IMAGE_JPEG),
  TXT(MediaType.TEXT_PLAIN),
  PNG(MediaType.IMAGE_PNG),
  PDF(MediaType.APPLICATION_PDF);

  private final MediaType mediaType;
}
