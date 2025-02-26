package org.example.projectmanagementapi.mapper;

import org.example.projectmanagementapi.dto.response.CommentDto;
import org.example.projectmanagementapi.entity.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CommentMapper {
    CommentDto toDto(Comment entity);
}
