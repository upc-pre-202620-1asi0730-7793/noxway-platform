package com.noctiva.noxway.commuting.infrastructure.persistence.jpa.repositories;

import com.noctiva.noxway.commuting.infrastructure.persistence.jpa.entities.AlertPersistenceEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertPersistenceRepository
    extends JpaRepository<AlertPersistenceEntity, UUID> {
  List<AlertPersistenceEntity> findByCommuteId(UUID value);

  List<AlertPersistenceEntity> findByIncidentId(UUID value);

  List<AlertPersistenceEntity> findByContactId(UUID value);

  List<AlertPersistenceEntity> findByStatus(String value);
}
