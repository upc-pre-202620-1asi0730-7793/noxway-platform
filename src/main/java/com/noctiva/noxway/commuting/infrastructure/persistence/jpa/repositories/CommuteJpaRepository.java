package com.noctiva.noxway.commuting.infrastructure.persistence.jpa.repositories;

import com.noctiva.noxway.commuting.infrastructure.persistence.jpa.entities.CommuteJpaEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommuteJpaRepository extends JpaRepository<CommuteJpaEntity, UUID> {
  java.util.List<CommuteJpaEntity> findByWorkerId(UUID value);

  java.util.List<CommuteJpaEntity> findByStatus(String value);
}
