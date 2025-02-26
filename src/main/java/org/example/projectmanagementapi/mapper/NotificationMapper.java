package org.example.projectmanagementapi.mapper;

import org.example.projectmanagementapi.dto.response.NotificationDto;
import org.example.projectmanagementapi.entity.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface NotificationMapper {
    NotificationDto toNotificationDto(Notification notification);
}
