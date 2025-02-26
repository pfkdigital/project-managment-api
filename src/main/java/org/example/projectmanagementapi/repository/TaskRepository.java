package org.example.projectmanagementapi.repository;

import java.util.Optional;
import org.example.projectmanagementapi.entity.Task;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface TaskRepository extends JpaRepository<Task, Integer> {

  @EntityGraph(attributePaths = {"attachments", "comments"})
  Optional<Task> findById(@Param("taskId") Integer taskId);
}
