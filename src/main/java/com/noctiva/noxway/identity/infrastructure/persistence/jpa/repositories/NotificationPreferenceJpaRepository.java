package com.noctiva.noxway.identity.infrastructure.persistence.jpa.repositories;

import com.noctiva.noxway.identity.infrastructure.persistence.jpa.entities.NotificationPreferenceJpaEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationPreferenceJpaRepository
    extends JpaRepository<NotificationPreferenceJpaEntity, UUID> {
  java.util.List<NotificationPreferenceJpaEntity> findByAccountId(UUID value);
}
