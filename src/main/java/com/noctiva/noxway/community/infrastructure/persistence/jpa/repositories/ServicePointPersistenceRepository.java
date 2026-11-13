package com.noctiva.noxway.community.infrastructure.persistence.jpa.repositories;

import com.noctiva.noxway.community.infrastructure.persistence.jpa.entities.ServicePointPersistenceEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServicePointPersistenceRepository
    extends JpaRepository<ServicePointPersistenceEntity, UUID> {
  List<ServicePointPersistenceEntity> findByReportedBy(UUID value);

  List<ServicePointPersistenceEntity> findByStatus(String value);
}
