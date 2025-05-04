package org.example.projectmanagementapi.service.impl;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.projectmanagementapi.config.WebSocketHandler;
import org.example.projectmanagementapi.dto.response.NotificationDto;
import org.example.projectmanagementapi.entity.Notification;
import org.example.projectmanagementapi.enums.NotificationType;
import org.example.projectmanagementapi.mapper.NotificationMapper;
import org.example.projectmanagementapi.repository.NotificationRepository;
import org.example.projectmanagementapi.service.NotificationService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

  private final NotificationRepository notificationRepository;
  private final NotificationMapper notificationMapper;
  private final WebSocketHandler webSocketHandler;

  @Override
  public Notification createNotification(Notification notification) {
    return notificationRepository.save(notification);
  }

  @Override
  public List<NotificationDto> getAllNotifications() {
    return notificationRepository.findAll().stream().map(notificationMapper::toNotificationDto).toList();
  }

  @Override
  public Notification getNotificationById(Integer id) {
    return notificationRepository
        .findById(id)
        .orElseThrow(
            () -> new EntityNotFoundException("Notification with id " + id + " not found"));
  }

  @Override
  public void deleteNotificationById(Integer id) {
    Notification notification =
        notificationRepository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Notification with id " + id + " not found"));
    notificationRepository.delete(notification);
  }

  @Override
  public Notification updateNotificationStatus(Integer id, Boolean status) {
    Notification notification =
        notificationRepository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Notification with id " + id + " not found"));
    notification.setIsRead(status);
    return notificationRepository.save(notification);
  }

  @Override
  public void createNotification(String message, NotificationType type) {
    Notification notification =
        Notification.builder()
            .message(message)
            .type(type)
            .isRead(false)
            .createdAt(LocalDate.now())
            .build();

    createNotification(notification);

    try {
      webSocketHandler.sendNotification(notification);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
