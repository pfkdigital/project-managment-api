package org.example.projectmanagementapi.repository;

import org.example.projectmanagementapi.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {}
