package com.noctiva.noxway.wellness.infrastructure.persistence.jpa.mappers;

import com.noctiva.noxway.wellness.domain.model.aggregates.SleepGoal;
import com.noctiva.noxway.wellness.infrastructure.persistence.jpa.entities.SleepGoalJpaEntity;

public final class SleepGoalPersistenceMapper {
  private SleepGoalPersistenceMapper() {}

  public static SleepGoal toDomain(SleepGoalJpaEntity e) {
    return new SleepGoal(e.id, e.workerId, e.targetMinutes, e.createdAt, e.version);
  }

  public static void copy(SleepGoal a, SleepGoalJpaEntity e) {
    e.id = a.id();
    e.workerId = a.workerId();
    e.targetMinutes = a.targetMinutes();
    e.createdAt = a.createdAt();
  }
}
