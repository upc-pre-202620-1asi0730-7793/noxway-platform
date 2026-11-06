package com.noctiva.noxway.moderation.infrastructure.persistence.jpa.repositories;

import com.noctiva.noxway.moderation.infrastructure.persistence.jpa.entities.ModerationActionJpaEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ModerationActionJpaRepository
    extends JpaRepository<ModerationActionJpaEntity, UUID> {
  java.util.List<ModerationActionJpaEntity> findByReportId(UUID value);

  java.util.List<ModerationActionJpaEntity> findByModeratorId(UUID value);
}
