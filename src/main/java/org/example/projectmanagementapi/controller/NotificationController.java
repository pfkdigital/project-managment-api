package org.example.projectmanagementapi.controller;

import org.example.projectmanagementapi.entity.Notification;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class NotificationController {

    @MessageMapping("/notify")
    @SendTo("/topic/notifications")
    public Notification sendNotification(Notification notification) {
        return notification;
    }
}