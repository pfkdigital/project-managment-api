package org.example.projectmanagementapi.controller;

import lombok.RequiredArgsConstructor;
import org.example.projectmanagementapi.dto.response.NotificationDto;
import org.example.projectmanagementapi.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

  private final NotificationService notificationService;

  @GetMapping
  public ResponseEntity<List<NotificationDto>> getNotifications() {
    return ResponseEntity.ok(notificationService.getAllNotifications());
  }
}
