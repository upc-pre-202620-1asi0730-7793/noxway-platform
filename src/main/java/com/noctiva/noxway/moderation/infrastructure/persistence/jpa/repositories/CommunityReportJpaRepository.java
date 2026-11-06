package com.noctiva.noxway.moderation.infrastructure.persistence.jpa.repositories;

import com.noctiva.noxway.moderation.infrastructure.persistence.jpa.entities.CommunityReportJpaEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunityReportJpaRepository
    extends JpaRepository<CommunityReportJpaEntity, UUID> {
  java.util.List<CommunityReportJpaEntity> findByReportedBy(UUID value);

  java.util.List<CommunityReportJpaEntity> findByTargetId(UUID value);

  java.util.List<CommunityReportJpaEntity> findByStatus(String value);
}
