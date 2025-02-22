package org.example.projectmanagementapi.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import org.example.projectmanagementapi.entity.Notification;
import org.example.projectmanagementapi.repository.NotificationRepository;
import org.example.projectmanagementapi.service.impl.NotificationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class NotificationServiceTest {

  @Mock private NotificationRepository notificationRepository;

  @InjectMocks private NotificationServiceImpl notificationService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void createNotification_savesNotification() {
    Notification notification = new Notification();
    when(notificationRepository.save(notification)).thenReturn(notification);

    Notification createdNotification = notificationService.createNotification(notification);

    assertNotNull(createdNotification);
    verify(notificationRepository, times(1)).save(notification);
  }

  @Test
  void getAllNotifications_returnsAllNotifications() {
    List<Notification> notifications = List.of(new Notification(), new Notification());
    when(notificationRepository.findAll()).thenReturn(notifications);

    List<Notification> result = notificationService.getAllNotifications();

    assertEquals(notifications.size(), result.size());
    verify(notificationRepository, times(1)).findAll();
  }

  @Test
  void getNotificationById_returnsNotification() {
    Notification notification = new Notification();
    when(notificationRepository.findById(1)).thenReturn(Optional.of(notification));

    Notification result = notificationService.getNotificationById(1);

    assertNotNull(result);
    assertEquals(notification, result);
    verify(notificationRepository, times(1)).findById(1);
  }

  @Test
  void getNotificationById_throwsExceptionWhenNotFound() {
    when(notificationRepository.findById(1)).thenReturn(Optional.empty());

    assertThrows(RuntimeException.class, () -> notificationService.getNotificationById(1));
    verify(notificationRepository, times(1)).findById(1);
  }

  @Test
  void deleteNotificationById_deletesNotification() {
    Notification notification = new Notification();
    when(notificationRepository.findById(1)).thenReturn(Optional.of(notification));

    notificationService.deleteNotificationById(1);

    verify(notificationRepository, times(1)).delete(notification);
  }

  @Test
  void deleteNotificationById_throwsExceptionWhenNotFound() {
    when(notificationRepository.findById(1)).thenReturn(Optional.empty());

    assertThrows(RuntimeException.class, () -> notificationService.deleteNotificationById(1));
    verify(notificationRepository, times(1)).findById(1);
  }

  @Test
  void updateNotificationStatus_updatesStatus() {
    Notification notification = new Notification();
    when(notificationRepository.findById(1)).thenReturn(Optional.of(notification));
    when(notificationRepository.save(notification)).thenReturn(notification);

    Notification updatedNotification = notificationService.updateNotificationStatus(1, true);

    assertNotNull(updatedNotification);
    assertTrue(updatedNotification.getIsRead());
    verify(notificationRepository, times(1)).findById(1);
    verify(notificationRepository, times(1)).save(notification);
  }

  @Test
  void updateNotificationStatus_throwsExceptionWhenNotFound() {
    when(notificationRepository.findById(1)).thenReturn(Optional.empty());

    assertThrows(
        RuntimeException.class, () -> notificationService.updateNotificationStatus(1, true));
    verify(notificationRepository, times(1)).findById(1);
  }
}
