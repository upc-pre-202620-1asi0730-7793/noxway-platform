package com.noctiva.noxway.iam.infrastructure.persistence.jpa.repositories;

import com.noctiva.noxway.iam.infrastructure.persistence.jpa.entities.AuthSessionPersistenceEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthSessionPersistenceRepository
    extends JpaRepository<AuthSessionPersistenceEntity, UUID> {
  Optional<AuthSessionPersistenceEntity> findByTokenHash(String tokenHash);

  List<AuthSessionPersistenceEntity> findByAccountId(UUID accountId);
}
