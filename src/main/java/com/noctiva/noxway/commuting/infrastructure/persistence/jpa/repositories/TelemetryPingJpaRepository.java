package com.noctiva.noxway.commuting.infrastructure.persistence.jpa.repositories;

import com.noctiva.noxway.commuting.infrastructure.persistence.jpa.entities.TelemetryPingJpaEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TelemetryPingJpaRepository extends JpaRepository<TelemetryPingJpaEntity, UUID> {
  java.util.List<TelemetryPingJpaEntity> findByCommuteId(UUID value);
}
