package com.noctiva.noxway.wellness.domain.model.aggregates;

import java.time.Instant;
import java.util.UUID;

public record SleepGoal(
    UUID id, UUID workerId, int targetMinutes, Instant createdAt, long version) {
  public static SleepGoal create(UUID workerId, int targetMinutes) {
    return new SleepGoal(UUID.randomUUID(), workerId, targetMinutes, Instant.now(), 0);
  }

  public SleepGoal withWorkerId(UUID value) {
    return new SleepGoal(id, value, targetMinutes, createdAt, version);
  }

  public SleepGoal withTargetMinutes(int value) {
    return new SleepGoal(id, workerId, value, createdAt, version);
  }
}
