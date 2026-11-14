package com.noctiva.noxway.commuting.infrastructure.persistence.jpa.repositories;

import com.noctiva.noxway.commuting.infrastructure.persistence.jpa.entities.SafetyIncidentPersistenceEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SafetyIncidentPersistenceRepository
    extends JpaRepository<SafetyIncidentPersistenceEntity, UUID> {
  List<SafetyIncidentPersistenceEntity> findByCommuteId(UUID value);

  List<SafetyIncidentPersistenceEntity> findByWorkerId(UUID value);

  List<SafetyIncidentPersistenceEntity> findByStatus(String value);
}
