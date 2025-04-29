package org.example.projectmanagementapi.repository;

import java.util.Optional;
import org.example.projectmanagementapi.entity.Task;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TaskRepository extends JpaRepository<Task, Integer> {

  @Query("select t from Task t LEFT join fetch User u ")
  Optional<Task> findTaskById(@Param("taskId") Integer taskId);
}
