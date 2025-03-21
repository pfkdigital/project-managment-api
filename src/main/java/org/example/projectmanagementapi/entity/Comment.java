package org.example.projectmanagementapi.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class Comment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "content", nullable = false)
  private String content;

  @Column(name = "created_at", nullable = false, updatable = false)
  @CreatedDate
  private LocalDate createdAt;

  @Column(name = "updated_at", nullable = true, updatable = true)
  @LastModifiedDate
  private LocalDate updatedAt;

  @Column(name = "is_edited", nullable = false)
  private Boolean isEdited;

  @ManyToOne()
  @JoinColumn(name = "task_id", nullable = true)
  private Task task;

  @ManyToOne()
  @JoinColumn(name = "issue_id", nullable = true)
  private Issue issue;

  @ManyToOne()
  @JoinColumn(name = "user_id", nullable = false)
  private User author;

  @ManyToOne()
  @JoinColumn(name ="project_id", nullable = true)
  private Project project;
}
