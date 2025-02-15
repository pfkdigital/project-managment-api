package org.example.projectmanagementapi.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.projectmanagementapi.enums.ProjectStatus;

import java.util.ArrayList;
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

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "owner_id")
    private User owner;

    @ManyToMany()
    @JoinTable(
            name = "project_user",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @ToString.Exclude
    private List<User> users = new ArrayList<>();

    @OneToMany(mappedBy = "project", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Task> tasks;


    @OneToMany(mappedBy = "project", fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    private List<Issue> issues;

    public void addUser(User user) {
        if (users == null) {
            users = new ArrayList<>();
        }
        users.add(user);
        user.getProjects().add(this);
    }

    public void removeUser(User user) {
        if (users != null) {
            users.remove(user);
            user.getProjects().remove(this);
        }
    }

    public void addTask(Task task) {
        if (tasks == null) {
            tasks = new ArrayList<>();
        }
        tasks.add(task);
        task.setProject(this);
    }

    public void addIssue(Issue issue) {
        if (issues == null) {
            issues = new ArrayList<>();
        }
        issues.add(issue);
        issue.setProject(this);
    }
}
