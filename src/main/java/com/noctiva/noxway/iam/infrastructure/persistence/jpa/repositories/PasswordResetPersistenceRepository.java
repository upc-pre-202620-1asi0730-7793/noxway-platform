package com.noctiva.noxway.iam.infrastructure.persistence.jpa.repositories;

import com.noctiva.noxway.iam.infrastructure.persistence.jpa.entities.PasswordResetPersistenceEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordResetPersistenceRepository
    extends JpaRepository<PasswordResetPersistenceEntity, UUID> {
  Optional<PasswordResetPersistenceEntity> findByTokenHash(String tokenHash);

  List<PasswordResetPersistenceEntity> findByAccountId(UUID accountId);
}
