package org.example.projectmanagementapi.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.*;
import org.example.projectmanagementapi.enums.NotificationType;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class Notification extends Auditable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "type", nullable = false, updatable = false)
  @Enumerated(EnumType.STRING)
  private NotificationType type;

  @Column(name = "message", nullable = false)
  private String message;

  @Column(name = "is_read", nullable = false)
  private Boolean isRead;
}
