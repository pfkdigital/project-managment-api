package org.example.projectmanagementapi.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.example.projectmanagementapi.config.WebSocketHandler;
import org.example.projectmanagementapi.dto.response.NotificationDto;
import org.example.projectmanagementapi.entity.Notification;
import org.example.projectmanagementapi.enums.NotificationType;
import org.example.projectmanagementapi.mapper.NotificationMapper;
import org.example.projectmanagementapi.repository.NotificationRepository;
import org.example.projectmanagementapi.service.impl.NotificationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

  @Mock
  private NotificationRepository notificationRepository;

  @Mock
  private NotificationMapper notificationMapper;

  @Mock
  private WebSocketHandler webSocketHandler;

  @InjectMocks
  private NotificationServiceImpl notificationService;

  private Notification notification;
  private NotificationDto notificationDto;

  @BeforeEach
  void setUp() {
    notification = Notification.builder()
            .id(1)
            .message("Test notification")
            .type(NotificationType.CREATION)
            .isRead(false)
            .createdAt(LocalDate.now())
            .build();

    notificationDto = new NotificationDto();
    notificationDto.setId(1);
    notificationDto.setMessage("Test notification");
    notificationDto.setType(NotificationType.CREATION);
    notificationDto.setIsRead(false);
    notificationDto.setCreatedAt(LocalDate.now().toString());
  }

  @Test
  void createNotification_savesAndReturnsNotification() {
    // Arrange
    when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

    // Act
    Notification result = notificationService.createNotification(notification);

    // Assert
    assertNotNull(result);
    assertEquals(notification.getId(), result.getId());
    assertEquals(notification.getMessage(), result.getMessage());
    verify(notificationRepository).save(notification);
  }

  @Test
  void getAllNotifications_returnsAllNotifications() {
    // Arrange
    List<Notification> notifications = List.of(notification);
    when(notificationRepository.findAll()).thenReturn(notifications);
    when(notificationMapper.toNotificationDto(notification)).thenReturn(notificationDto);

    // Act
    List<NotificationDto> result = notificationService.getAllNotifications();

    // Assert
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(notificationDto.getId(), result.get(0).getId());
    assertEquals(notificationDto.getMessage(), result.get(0).getMessage());
    verify(notificationRepository).findAll();
    verify(notificationMapper).toNotificationDto(notification);
  }

  @Test
  void getNotificationById_returnsNotification() {
    // Arrange
    when(notificationRepository.findById(1)).thenReturn(Optional.of(notification));

    // Act
    Notification result = notificationService.getNotificationById(1);

    // Assert
    assertNotNull(result);
    assertEquals(notification.getId(), result.getId());
    assertEquals(notification.getMessage(), result.getMessage());
    verify(notificationRepository).findById(1);
  }

  @Test
  void getNotificationById_throwsExceptionWhenNotFound() {
    // Arrange
    when(notificationRepository.findById(1)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(RuntimeException.class, () -> notificationService.getNotificationById(1));
    verify(notificationRepository).findById(1);
  }

  @Test
  void deleteNotificationById_deletesNotification() {
    // Arrange
    when(notificationRepository.findById(1)).thenReturn(Optional.of(notification));

    // Act
    notificationService.deleteNotificationById(1);

    // Assert
    verify(notificationRepository).findById(1);
    verify(notificationRepository).delete(notification);
  }

  @Test
  void deleteNotificationById_throwsExceptionWhenNotFound() {
    // Arrange
    when(notificationRepository.findById(1)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(RuntimeException.class, () -> notificationService.deleteNotificationById(1));
    verify(notificationRepository).findById(1);
    verify(notificationRepository, never()).delete(any(Notification.class));
  }

  @Test
  void updateNotificationStatus_updatesAndReturnsNotification() {
    // Arrange
    when(notificationRepository.findById(1)).thenReturn(Optional.of(notification));
    when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

    // Act
    Notification result = notificationService.updateNotificationStatus(1, true);

    // Assert
    assertNotNull(result);
    assertTrue(result.getIsRead());
    verify(notificationRepository).findById(1);
    verify(notificationRepository).save(notification);
  }

  @Test
  void updateNotificationStatus_throwsExceptionWhenNotFound() {
    // Arrange
    when(notificationRepository.findById(1)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(RuntimeException.class, () -> notificationService.updateNotificationStatus(1, true));
    verify(notificationRepository).findById(1);
    verify(notificationRepository, never()).save(any(Notification.class));
  }

  @Test
  void createNotification_withMessageAndType_createsAndSendsNotification() throws Exception {
    // Arrange
    when(notificationRepository.save(any(Notification.class))).thenReturn(notification);
    doNothing().when(webSocketHandler).sendNotification(any(Notification.class));

    // Act
    notificationService.createNotification("Test notification", NotificationType.CREATION);

    // Assert
    verify(notificationRepository).save(any(Notification.class));
    verify(webSocketHandler).sendNotification(any(Notification.class));
  }

  @Test
  void createNotification_withMessageAndType_handlesWebSocketException() throws Exception {
    // Arrange
    when(notificationRepository.save(any(Notification.class))).thenReturn(notification);
    doThrow(new RuntimeException("WebSocket error")).when(webSocketHandler).sendNotification(any(Notification.class));

    // Act & Assert
    assertDoesNotThrow(() -> notificationService.createNotification("Test notification", NotificationType.CREATION));
    verify(notificationRepository).save(any(Notification.class));
    verify(webSocketHandler).sendNotification(any(Notification.class));
  }
}