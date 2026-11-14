package com.noctiva.noxway.wellness.infrastructure.persistence.jpa.repositories;

import com.noctiva.noxway.wellness.infrastructure.persistence.jpa.entities.RestSessionPersistenceEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestSessionPersistenceRepository
    extends JpaRepository<RestSessionPersistenceEntity, UUID> {
  List<RestSessionPersistenceEntity> findByWorkerId(UUID workerId);
}
