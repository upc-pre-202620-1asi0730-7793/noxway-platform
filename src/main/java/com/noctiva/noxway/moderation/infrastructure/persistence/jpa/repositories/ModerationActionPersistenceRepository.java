package com.noctiva.noxway.moderation.infrastructure.persistence.jpa.repositories;

import com.noctiva.noxway.moderation.infrastructure.persistence.jpa.entities.ModerationActionPersistenceEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ModerationActionPersistenceRepository
    extends JpaRepository<ModerationActionPersistenceEntity, UUID> {
  List<ModerationActionPersistenceEntity> findByReportId(UUID value);

  List<ModerationActionPersistenceEntity> findByModeratorId(UUID value);
}
