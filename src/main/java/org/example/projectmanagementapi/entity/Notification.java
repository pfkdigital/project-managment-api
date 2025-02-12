package org.example.projectmanagementapi.entity;


import jakarta.persistence.*;
import lombok.*;
import org.example.projectmanagementapi.enums.NotificationType;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDate;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;

    @Column(name = "type", nullable = false, updatable = false)
    private NotificationType type;

    @Column(name = "message", nullable = false)
    private String message;

    @Column(name = "is_read", nullable = false)
    private Boolean isRead;

    @Column(name = "created_at", nullable = false)
    @CreatedDate
    private LocalDate createdAt;
}
