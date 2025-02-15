package org.example.projectmanagementapi.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.projectmanagementapi.entity.Notification;
import org.example.projectmanagementapi.enums.NotificationType;
import org.example.projectmanagementapi.repository.NotificationRepository;
import org.example.projectmanagementapi.service.NotificationService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate simpleMessageTemplate;

    @Override
    public Notification createNotification(Notification notification) {
        return notificationRepository.save(notification);
    }

    @Override
    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    @Override
    public Notification getNotificationById(Integer id) {
        return notificationRepository.findById(id).orElseThrow(() -> new RuntimeException("Notification with id " + id + " not found"));
    }

    @Override
    public void deleteNotificationById(Integer id) {
        Notification notification = notificationRepository.findById(id).orElseThrow(() -> new RuntimeException("Notification with id " + id + " not found"));
        notificationRepository.delete(notification);
    }

    @Override
    public Notification updateNotificationStatus(Integer id, Boolean status) {
        Notification notification = notificationRepository.findById(id).orElseThrow(() -> new RuntimeException("Notification with id " + id + " not found"));
        notification.setIsRead(status);
        return notificationRepository.save(notification);
    }

    @Override
    public void createNotification(String message, NotificationType type) {
        Notification notification = Notification.builder()
                .message(message)
                .type(type)
                .isRead(false)
                .build();

        createNotification(notification);
        simpleMessageTemplate.convertAndSend("/topic/notifications", notification);
    }
}
