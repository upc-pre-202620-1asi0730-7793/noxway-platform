package com.noctiva.noxway.commuting.infrastructure.persistence.jpa.repositories;

import com.noctiva.noxway.commuting.infrastructure.persistence.jpa.entities.CommutePersistenceEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommutePersistenceRepository
    extends JpaRepository<CommutePersistenceEntity, UUID> {
  List<CommutePersistenceEntity> findByWorkerId(UUID value);

  List<CommutePersistenceEntity> findByStatus(String value);
}
