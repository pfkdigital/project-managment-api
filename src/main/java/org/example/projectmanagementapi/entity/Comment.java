package org.example.projectmanagementapi.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class Comment extends Auditable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "content", nullable = false)
  private String content;

  @ManyToOne()
  @JoinColumn(name = "task_id", nullable = true)
  private Task task;

  @ManyToOne()
  @JoinColumn(name = "issue_id", nullable = true)
  private Issue issue;

  @ManyToOne()
  @JoinColumn(name = "user_id", nullable = false)
  private User author;
}
