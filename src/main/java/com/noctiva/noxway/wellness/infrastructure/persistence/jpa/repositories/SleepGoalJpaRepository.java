package com.noctiva.noxway.wellness.infrastructure.persistence.jpa.repositories;

import com.noctiva.noxway.wellness.infrastructure.persistence.jpa.entities.SleepGoalJpaEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SleepGoalJpaRepository extends JpaRepository<SleepGoalJpaEntity, UUID> {
  java.util.List<SleepGoalJpaEntity> findByWorkerId(UUID value);
}
