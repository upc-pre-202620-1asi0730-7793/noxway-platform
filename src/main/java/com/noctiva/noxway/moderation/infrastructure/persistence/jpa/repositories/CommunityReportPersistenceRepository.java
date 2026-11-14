package com.noctiva.noxway.moderation.infrastructure.persistence.jpa.repositories;

import com.noctiva.noxway.moderation.infrastructure.persistence.jpa.entities.CommunityReportPersistenceEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunityReportPersistenceRepository
    extends JpaRepository<CommunityReportPersistenceEntity, UUID> {
  List<CommunityReportPersistenceEntity> findByReportedBy(UUID value);

  List<CommunityReportPersistenceEntity> findByTargetId(UUID value);

  List<CommunityReportPersistenceEntity> findByStatus(String value);
}
