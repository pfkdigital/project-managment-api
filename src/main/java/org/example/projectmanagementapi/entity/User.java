package org.example.projectmanagementapi.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.projectmanagementapi.enums.Role;

import java.util.List;

@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "display_image_url")
    private String displayImageUrl;

    @OneToMany(mappedBy = "owner")
    private List<Project> ownedProjects;

    @ManyToMany(mappedBy = "users")
    private List<Project> projects;

    @ManyToMany(mappedBy = "users")
    private List<Task> tasks;

    @OneToMany(mappedBy = "assignedTo")
    private List<Issue> assignedIssues;

    @OneToMany(mappedBy = "author")
    private List<Comment> comments;
}
