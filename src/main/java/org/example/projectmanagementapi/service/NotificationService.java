package org.example.projectmanagementapi.service;

import java.util.List;

import org.example.projectmanagementapi.dto.response.NotificationDto;
import org.example.projectmanagementapi.entity.Notification;
import org.example.projectmanagementapi.enums.NotificationType;

public interface NotificationService {
  Notification createNotification(Notification notification);

  List<NotificationDto> getAllNotifications();

  Notification getNotificationById(Integer id);

  void deleteNotificationById(Integer id);

  Notification updateNotificationStatus(Integer id, Boolean status);

  void createNotification(String message, NotificationType type);
}
