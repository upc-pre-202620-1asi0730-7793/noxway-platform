package com.noctiva.noxway.wellness.infrastructure.persistence.jpa.repositories;

import com.noctiva.noxway.wellness.infrastructure.persistence.jpa.entities.SleepGoalPersistenceEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SleepGoalPersistenceRepository
    extends JpaRepository<SleepGoalPersistenceEntity, UUID> {
  List<SleepGoalPersistenceEntity> findByWorkerId(UUID workerId);
}
