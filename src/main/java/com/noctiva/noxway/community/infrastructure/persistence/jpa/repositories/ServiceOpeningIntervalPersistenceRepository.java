package com.noctiva.noxway.community.infrastructure.persistence.jpa.repositories;

import com.noctiva.noxway.community.infrastructure.persistence.jpa.entities.ServiceOpeningIntervalPersistenceEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceOpeningIntervalPersistenceRepository
    extends JpaRepository<ServiceOpeningIntervalPersistenceEntity, UUID> {
  List<ServiceOpeningIntervalPersistenceEntity> findByServiceId(UUID value);
}
