package org.example.projectmanagementapi.repository;

import org.example.projectmanagementapi.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Integer> {}
