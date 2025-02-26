package org.example.projectmanagementapi.mapper;

import org.example.projectmanagementapi.dto.response.AttachmentDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.example.projectmanagementapi.entity.Attachment;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AttachmentMapper {
  AttachmentDto toAttachmentDto(Attachment attachment);
}
