package org.example.projectmanagementapi.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.projectmanagementapi.enums.IssueStatus;
import org.example.projectmanagementapi.enums.PriorityStatus;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Issue {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;

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

    @OneToOne()
    @JoinColumn(name = "reported_by_id")
    private User reportedBy;

    @ManyToOne()
    @JoinColumn(name = "assigned_to_id")
    private User assignedTo;

    @OneToMany(mappedBy = "issue")
    private List<Comment> comments;

    @OneToMany(mappedBy = "issue")
    private List<Attachment> attachments;
}
