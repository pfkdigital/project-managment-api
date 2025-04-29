package org.example.projectmanagementapi.repository;

import org.example.projectmanagementapi.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Integer> {
    @Query("""
    SELECT p FROM project p
    LEFT JOIN FETCH p.owner
    WHERE p.id = :id
""")
    Optional<Project> findProjectWithOwnerById(@Param("id") Integer id);

    @Query("""
    SELECT p FROM project p
    LEFT JOIN FETCH p.collaborators
    WHERE p.id = :id
""")
    Optional<Project> findProjectWithCollaboratorsById(@Param("id") Integer id);

    @Query("""
    SELECT p FROM project p
    LEFT JOIN FETCH p.tasks
    WHERE p.id = :id
""")
    Optional<Project> findProjectWithTasksById(@Param("id") Integer id);

    @Query("""
    SELECT p FROM project p
    LEFT JOIN FETCH p.issues
    WHERE p.id = :id
""")
    Optional<Project> findProjectWithIssuesById(@Param("id") Integer id);
}
