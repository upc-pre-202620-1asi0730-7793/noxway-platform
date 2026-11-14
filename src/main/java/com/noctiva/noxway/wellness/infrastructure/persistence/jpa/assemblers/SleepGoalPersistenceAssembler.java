package com.noctiva.noxway.wellness.infrastructure.persistence.jpa.assemblers;

import com.noctiva.noxway.wellness.domain.model.aggregates.SleepGoal;
import com.noctiva.noxway.wellness.infrastructure.persistence.jpa.entities.SleepGoalPersistenceEntity;

public final class SleepGoalPersistenceAssembler {
  private SleepGoalPersistenceAssembler() {}

  public static SleepGoal toDomain(SleepGoalPersistenceEntity e) {
    return SleepGoal.reconstitute(
        e.getId(), e.getWorkerId(), e.getTargetMinutes(), e.getCreatedAt(), e.getVersion());
  }

  public static SleepGoalPersistenceEntity toPersistence(SleepGoal g) {
    var e = new SleepGoalPersistenceEntity();
    e.setId(g.getId());
    e.setWorkerId(g.getWorkerId());
    e.setTargetMinutes(g.getTargetMinutes());
    e.setCreatedAt(g.getCreatedAt());
    e.setVersion(g.getVersion());
    return e;
  }
}
