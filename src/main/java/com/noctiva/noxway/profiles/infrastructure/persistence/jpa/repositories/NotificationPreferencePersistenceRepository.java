package com.noctiva.noxway.profiles.infrastructure.persistence.jpa.repositories;

import com.noctiva.noxway.profiles.infrastructure.persistence.jpa.entities.NotificationPreferencePersistenceEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationPreferencePersistenceRepository
    extends JpaRepository<NotificationPreferencePersistenceEntity, UUID> {
  List<NotificationPreferencePersistenceEntity> findByAccountId(UUID accountId);

  Optional<NotificationPreferencePersistenceEntity> findByAccountIdAndChannel(
      UUID accountId, String channel);
}
