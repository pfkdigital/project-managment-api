package org.example.projectmanagementapi.repository;

import org.example.projectmanagementapi.dto.response.DetailedIssueDto;
import org.example.projectmanagementapi.entity.Issue;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IssueRepository extends JpaRepository<Issue, Integer> {
  @EntityGraph(attributePaths = {"reportedBy", "assignedTo, project, comments,attachments"})
  Optional<Issue> findById(Integer id);
}
