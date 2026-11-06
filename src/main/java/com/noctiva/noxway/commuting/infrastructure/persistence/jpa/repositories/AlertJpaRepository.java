package com.noctiva.noxway.commuting.infrastructure.persistence.jpa.repositories;

import com.noctiva.noxway.commuting.infrastructure.persistence.jpa.entities.AlertJpaEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertJpaRepository extends JpaRepository<AlertJpaEntity, UUID> {
  java.util.List<AlertJpaEntity> findByCommuteId(UUID value);

  java.util.List<AlertJpaEntity> findByIncidentId(UUID value);

  java.util.List<AlertJpaEntity> findByContactId(UUID value);

  java.util.List<AlertJpaEntity> findByStatus(String value);
}
