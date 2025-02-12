package org.example.projectmanagementapi.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDate;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "created_at", nullable = false)
    @CreatedDate
    private LocalDate createdAt;

    @ManyToOne()
    @JoinColumn(name = "task_id", nullable = true)
    private Task task;

    @ManyToOne()
    @JoinColumn(name = "issue_id", nullable = true)
    private Issue issue;
}
