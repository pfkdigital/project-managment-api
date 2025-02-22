package org.example.projectmanagementapi.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.*;
import org.example.projectmanagementapi.enums.PriorityStatus;
import org.example.projectmanagementapi.enums.TaskStatus;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class Task {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "description", nullable = false)
  private String description;

  @Column(name = "due_date", nullable = false)
  private LocalDate dueDate;

  @Column(name = "priority", nullable = false, updatable = false)
  @Enumerated(EnumType.STRING)
  private PriorityStatus priority;

  @Column(name = "status", nullable = false)
  @Enumerated(EnumType.STRING)
  private TaskStatus status;

  @ManyToOne()
  @JoinColumn(name = "project_id")
  private Project project;

  @ManyToMany()
  @JoinTable(
      name = "task_user",
      joinColumns = @JoinColumn(name = "task_id"),
      inverseJoinColumns = @JoinColumn(name = "user_id"))
  @ToString.Exclude
  private List<User> users;

  @OneToMany(mappedBy = "task")
  @ToString.Exclude
  private List<Comment> comments;

  @OneToMany(mappedBy = "task")
  @ToString.Exclude
  private List<Attachment> attachments;

  public void addAttachment(Attachment attachment) {
    if (attachments == null) {
      attachments = new ArrayList<>();
    }
    attachments.add(attachment);
    attachment.setTask(this);
  }

  public void addComment(Comment comment) {
    if (comments == null) {
      comments = new ArrayList<>();
    }
    comments.add(comment);
    comment.setTask(this);
  }
}
