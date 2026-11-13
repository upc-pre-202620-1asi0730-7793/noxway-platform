package com.noctiva.noxway.commuting.infrastructure.persistence.jpa.repositories;

import com.noctiva.noxway.commuting.infrastructure.persistence.jpa.entities.TelemetryPingPersistenceEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TelemetryPingPersistenceRepository
    extends JpaRepository<TelemetryPingPersistenceEntity, UUID> {
  List<TelemetryPingPersistenceEntity> findByCommuteId(UUID value);
}
