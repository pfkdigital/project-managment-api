package org.example.projectmanagementapi.service;

import org.example.projectmanagementapi.entity.Notification;

import java.util.List;

public interface NotificationService {
    Notification createNotification(Notification notification);
    List<Notification> getAllNotifications();
    Notification getNotificationById(Integer id);
    void deleteNotificationById(Integer id);
    Notification updateNotificationStatus(Integer id, Boolean status);
}
