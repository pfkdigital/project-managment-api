package org.example.projectmanagementapi.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.example.projectmanagementapi.entity.Notification;
import org.example.projectmanagementapi.enums.NotificationType;
import org.example.projectmanagementapi.repository.NotificationRepository;
import org.example.projectmanagementapi.service.impl.NotificationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {

  @Mock private NotificationRepository notificationRepository;

  @InjectMocks private NotificationServiceImpl notificationService;

  private Notification notification;

  @BeforeEach
  void setUp() {
    notification = new Notification();
    notification.setId(1);
    notification.setMessage("Test Notification");
    notification.setType(NotificationType.CREATION);
  }

  @Test
  void testCreateNotification() {
    when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

    notificationService.createNotification(notification);

    verify(notificationRepository, times(1)).save(any(Notification.class));
  }

  @Test
  void testGetNotificationById() {
    when(notificationRepository.findById(any(Integer.class))).thenReturn(Optional.of(notification));

    notificationService.getNotificationById(1);

    verify(notificationRepository, times(1)).findById(1);
  }

  @Test
  void testDeleteNotification() {
    when(notificationRepository.findById(any(Integer.class))).thenReturn(Optional.of(notification));

    notificationService.deleteNotificationById(1);

    verify(notificationRepository, times(1)).delete(any(Notification.class));
  }
}