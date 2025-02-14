package org.example.projectmanagementapi.repository;

import org.example.projectmanagementapi.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task,Integer> {

    @Query("SELECT t FROM Task t LEFT JOIN FETCH t.attachments LEFT JOIN FETCH t.comments WHERE t.id = :taskId")
    Optional<Task> getTaskByIdWithAttachmentAndComments(@Param("taskId") Integer taskId);
}
