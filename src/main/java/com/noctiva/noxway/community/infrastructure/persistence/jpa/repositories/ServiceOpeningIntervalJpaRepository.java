package com.noctiva.noxway.community.infrastructure.persistence.jpa.repositories;

import com.noctiva.noxway.community.infrastructure.persistence.jpa.entities.ServiceOpeningIntervalJpaEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceOpeningIntervalJpaRepository
    extends JpaRepository<ServiceOpeningIntervalJpaEntity, UUID> {
  java.util.List<ServiceOpeningIntervalJpaEntity> findByServiceId(UUID value);
}
