package org.example.projectmanagementapi.entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import lombok.*;
import org.example.projectmanagementapi.enums.IssueStatus;
import org.example.projectmanagementapi.enums.PriorityStatus;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class Issue implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "title", nullable = false)
  private String title;

  @Column(name = "description", nullable = false)
  private String description;

  @Column(name = "status", nullable = false)
  @Enumerated(EnumType.STRING)
  private IssueStatus status;

  @Column(name = "priority_status", nullable = false, updatable = false)
  @Enumerated(EnumType.STRING)
  private PriorityStatus priorityStatus;

  @ManyToOne()
  @JoinColumn(name = "project_id")
  private Project project;

  @ManyToOne()
  @JoinColumn(name = "reported_by_id")
  private User reportedBy;

  @ManyToOne()
  @JoinColumn(name = "assigned_to_id")
  private User assignedTo;

  @OneToMany(mappedBy = "issue", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Comment> comments;

  @OneToMany(mappedBy = "issue", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Attachment> attachments;

  public void addComment(Comment comment) {
    if (comments == null) {
      comments = new ArrayList<>();
    }
    comments.add(comment);
    comment.setIssue(this);
  }

  public void addAttachment(Attachment attachment) {
    if (attachments == null) {
      attachments = new ArrayList<>();
    }
    attachments.add(attachment);
    attachment.setIssue(this);
  }
}
