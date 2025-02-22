package org.example.projectmanagementapi.repository;

import java.util.Optional;
import org.example.projectmanagementapi.entity.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Integer> {
  Optional<VerificationToken> findByToken(String token);

  VerificationToken findByUserId(Integer userId);
}
