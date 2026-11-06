package com.noctiva.noxway.identity.infrastructure.persistence.jpa.repositories;

import com.noctiva.noxway.identity.infrastructure.persistence.jpa.entities.AuthSessionJpaEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthSessionJpaRepository extends JpaRepository<AuthSessionJpaEntity, UUID> {
  java.util.List<AuthSessionJpaEntity> findByAccountId(UUID value);

  java.util.List<AuthSessionJpaEntity> findByTokenHash(String value);
}
