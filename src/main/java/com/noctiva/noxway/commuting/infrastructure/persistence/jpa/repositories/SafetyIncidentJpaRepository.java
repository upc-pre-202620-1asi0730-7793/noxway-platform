package com.noctiva.noxway.commuting.infrastructure.persistence.jpa.repositories;

import com.noctiva.noxway.commuting.infrastructure.persistence.jpa.entities.SafetyIncidentJpaEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SafetyIncidentJpaRepository extends JpaRepository<SafetyIncidentJpaEntity, UUID> {
  java.util.List<SafetyIncidentJpaEntity> findByCommuteId(UUID value);

  java.util.List<SafetyIncidentJpaEntity> findByWorkerId(UUID value);

  java.util.List<SafetyIncidentJpaEntity> findByStatus(String value);
}
