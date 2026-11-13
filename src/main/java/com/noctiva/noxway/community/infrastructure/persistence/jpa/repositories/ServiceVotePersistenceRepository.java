package com.noctiva.noxway.community.infrastructure.persistence.jpa.repositories;

import com.noctiva.noxway.community.infrastructure.persistence.jpa.entities.ServiceVotePersistenceEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceVotePersistenceRepository
    extends JpaRepository<ServiceVotePersistenceEntity, UUID> {
  List<ServiceVotePersistenceEntity> findByServiceId(UUID value);

  List<ServiceVotePersistenceEntity> findByAccountId(UUID value);
}
