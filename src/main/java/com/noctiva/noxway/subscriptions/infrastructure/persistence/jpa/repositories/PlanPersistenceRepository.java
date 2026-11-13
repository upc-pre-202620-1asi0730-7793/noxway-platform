package com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.repositories;

import com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.entities.PlanPersistenceEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanPersistenceRepository extends JpaRepository<PlanPersistenceEntity, UUID> {
  List<PlanPersistenceEntity> findByCode(String value);
}
