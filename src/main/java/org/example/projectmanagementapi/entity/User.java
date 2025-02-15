package org.example.projectmanagementapi.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.projectmanagementapi.enums.Role;

import java.util.ArrayList;
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

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<Project> ownedProjects;

    @ManyToMany(mappedBy = "users")
    @ToString.Exclude
    private List<Project> projects;

    @ManyToMany(mappedBy = "users")
    @ToString.Exclude
    private List<Task> tasks;

    @OneToMany(mappedBy = "assignedTo", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    @ToString.Exclude
    private List<Issue> assignedIssues;

    @OneToMany(mappedBy = "reportedBy", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    @ToString.Exclude
    private List<Issue> reportedIssues;

    @OneToMany(mappedBy = "author",cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<Comment> comments;

    public void addOwnedProject(Project project) {
        if (projects == null) {
            ownedProjects = new ArrayList<>();
        }
        ownedProjects.add(project);
        project.setOwner(this);
    }

    public void addProject(Project project) {
        if (projects == null) {
            projects = new ArrayList<>();
        }
        projects.add(project);
        project.getUsers().add(this);
    }

    public void addTask(Task task) {
        if (tasks == null) {
            tasks = new ArrayList<>();
        }
        tasks.add(task);
        task.getUsers().add(this);
    }

    public void addReportedIssue(Issue issue) {
        if (reportedIssues == null) {
            reportedIssues = new ArrayList<>();
        }
        reportedIssues.add(issue);
        issue.setReportedBy(this);
    }

    public void addAssignedIssue(Issue issue) {
        if (assignedIssues == null) {
            assignedIssues = new ArrayList<>();
        }
        assignedIssues.add(issue);
        issue.setAssignedTo(this);
    }

    public void addComment(Comment comment) {
        if (comments == null) {
            comments = new ArrayList<>();
        }
        comments.add(comment);
        comment.setAuthor(this);
    }
}
