package org.example.projectmanagementapi.repository;

import org.example.projectmanagementapi.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Integer> {}
