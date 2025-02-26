package org.example.projectmanagementapi.repository;

import org.example.projectmanagementapi.entity.Project;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Integer> {
    @EntityGraph(attributePaths = {"users", "tasks", "issues"})
    Optional<Project> findById(Integer id);
}
