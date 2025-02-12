package org.example.projectmanagementapi.repository;

import org.example.projectmanagementapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Integer> {
}
