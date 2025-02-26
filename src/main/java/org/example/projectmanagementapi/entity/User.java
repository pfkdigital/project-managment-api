package org.example.projectmanagementapi.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.example.projectmanagementapi.enums.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class User implements UserDetails {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
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

  @Column(name = "enabled", nullable = false)
  private boolean enabled = true;

  @Column(name = "account_non_expired", nullable = false)
  private boolean accountNonExpired = true;

  @Column(name = "account_non_locked", nullable = false)
  private boolean accountNonLocked = true;

  @Column(name = "credentials_non_expired", nullable = false)
  private boolean credentialsNonExpired = true;

  @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
  @JsonIgnore
  private VerificationToken verificationToken;

  @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
  @JsonIgnore
  private PasswordResetToken passwordResetToken;

  @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
  @ToString.Exclude
  private List<Project> ownedProjects;

  @ManyToMany(mappedBy = "users")
  @ToString.Exclude
  private List<Project> projects;

  @ManyToMany(mappedBy = "users")
  @ToString.Exclude
  private List<Task> tasks;

  @OneToMany(
      mappedBy = "assignedTo",
      cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
  @ToString.Exclude
  private List<Issue> assignedIssues;

  @OneToMany(
      mappedBy = "reportedBy",
      cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
  @ToString.Exclude
  private List<Issue> reportedIssues;

  @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
  @ToString.Exclude
  private List<Comment> comments;

  @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
  @ToString.Exclude
  private List<Attachment> attachments;

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

  public void addAttachment(Attachment attachment) {
    if (attachments == null) {
      attachments = new ArrayList<>();
    }
    attachments.add(attachment);
    attachment.setAuthor(this);
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority(role.name()));
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return false;
  }
}
