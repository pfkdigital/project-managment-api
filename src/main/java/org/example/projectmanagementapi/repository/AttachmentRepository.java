package org.example.projectmanagementapi.repository;

import org.example.projectmanagementapi.entity.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttachmentRepository extends JpaRepository<Attachment, Integer> {
}
