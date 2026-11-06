package com.noctiva.noxway.identity.infrastructure.persistence.jpa.repositories;

import com.noctiva.noxway.identity.infrastructure.persistence.jpa.entities.PasswordResetJpaEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordResetJpaRepository extends JpaRepository<PasswordResetJpaEntity, UUID> {
  java.util.List<PasswordResetJpaEntity> findByAccountId(UUID value);

  java.util.List<PasswordResetJpaEntity> findByTokenHash(String value);
}
