package org.example.projectmanagementapi.repository;

import org.example.projectmanagementapi.entity.Issue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface IssueRepository extends JpaRepository<Issue, Integer> {
  @Query("SELECT i from Issue i LEFT JOIN FETCH i.comments WHERE i.id = :id")
  Optional<Issue> findTaskByIdWithComments(@Param("id") Integer id);

  @Query("SELECT i from Issue i LEFT JOIN FETCH i.attachments WHERE i.id = :id")
  Optional<Issue> findTaskByIdWithAttachments(@Param("id") Integer id);
}
