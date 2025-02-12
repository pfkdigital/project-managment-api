package org.example.projectmanagementapi.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.projectmanagementapi.enums.ProjectStatus;

import java.util.List;

@Entity(name = "project")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ProjectStatus status;

    @Column(name = "display_image_url", nullable = false)
    private String displayImageUrl;

    @ManyToOne()
    @JoinColumn(name = "owner_id")
    private User owner;

    @ManyToMany()
    @JoinTable(
            name = "project_user",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @ToString.Exclude
    private List<User> users;
}
