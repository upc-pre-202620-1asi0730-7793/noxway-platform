package com.noctiva.noxway.community.infrastructure.persistence.jpa.repositories;

import com.noctiva.noxway.community.infrastructure.persistence.jpa.entities.RiskZonePersistenceEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RiskZonePersistenceRepository
    extends JpaRepository<RiskZonePersistenceEntity, UUID> {
  List<RiskZonePersistenceEntity> findByReportedBy(UUID value);

  List<RiskZonePersistenceEntity> findByStatus(String value);
}
