package com.noctiva.noxway.community.infrastructure.persistence.jpa.repositories;

import com.noctiva.noxway.community.infrastructure.persistence.jpa.entities.RiskZoneJpaEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RiskZoneJpaRepository extends JpaRepository<RiskZoneJpaEntity, UUID> {
  java.util.List<RiskZoneJpaEntity> findByReportedBy(UUID value);

  java.util.List<RiskZoneJpaEntity> findByStatus(String value);
}
