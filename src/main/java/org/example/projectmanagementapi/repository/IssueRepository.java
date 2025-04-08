package org.example.projectmanagementapi.repository;

import org.example.projectmanagementapi.entity.Issue;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IssueRepository extends JpaRepository<Issue, Integer> {
  @EntityGraph(attributePaths = {"attachments", "comments"})
  Optional<Issue> findById(Integer id);
}
