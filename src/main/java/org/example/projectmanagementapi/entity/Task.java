package org.example.projectmanagementapi.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.projectmanagementapi.enums.PriorityStatus;
import org.example.projectmanagementapi.enums.TaskStatus;

import java.time.LocalDate;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
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

    @ManyToMany()
    @JoinTable(
            name ="task_user",
            joinColumns = @JoinColumn(name = "task_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> users;

    @OneToMany(mappedBy = "task")
    private List<Comment> comments;

    @OneToMany(mappedBy = "task")
    private List<Attachment> attachments;
}
