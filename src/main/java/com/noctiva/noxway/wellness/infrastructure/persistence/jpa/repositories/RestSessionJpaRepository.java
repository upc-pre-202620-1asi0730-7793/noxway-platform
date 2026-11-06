package com.noctiva.noxway.wellness.infrastructure.persistence.jpa.repositories;

import com.noctiva.noxway.wellness.infrastructure.persistence.jpa.entities.RestSessionJpaEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestSessionJpaRepository extends JpaRepository<RestSessionJpaEntity, UUID> {
  java.util.List<RestSessionJpaEntity> findByWorkerId(UUID value);
}
