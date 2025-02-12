package org.example.projectmanagementapi.repository;

import org.example.projectmanagementapi.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task,Integer> {
}
