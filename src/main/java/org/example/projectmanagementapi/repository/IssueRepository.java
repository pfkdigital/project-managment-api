package org.example.projectmanagementapi.repository;

import org.example.projectmanagementapi.entity.Issue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IssueRepository extends JpaRepository<Issue, Integer> {
}
