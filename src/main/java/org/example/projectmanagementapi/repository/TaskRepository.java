package org.example.projectmanagementapi.repository;

import java.util.Optional;
import org.example.projectmanagementapi.entity.Task;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TaskRepository extends JpaRepository<Task, Integer> {

  @Query("SELECT t from Task t LEFT JOIN FETCH t.users WHERE t.id = :id")
  Optional<Task> findTaskByIdWithUsers(@Param("id") Integer id);

  @Query("SELECT t from Task t LEFT JOIN FETCH t.attachments WHERE t.id = :id")
  Optional<Task> findTaskByIdWithAttachments(@Param("id") Integer id);

  @Query("SELECT t from Task t LEFT JOIN FETCH t.comments WHERE t.id = :id")
  Optional<Task> findTaskByIdWithComments(@Param("id") Integer id);
}
