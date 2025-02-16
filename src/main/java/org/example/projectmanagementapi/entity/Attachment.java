package org.example.projectmanagementapi.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class Attachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    @Column(name = "file_type", nullable = false)
    private String fileType;

    @Column(name="uploaded_At", nullable = false)
    private LocalDate uploadedAt;

    @ManyToOne()
    @JoinColumn(name = "task_id", nullable = true)
    private Issue issue;

    @ManyToOne()
    @JoinColumn(name = "issue_id", nullable = true)
    private Task task;
}
