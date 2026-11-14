package com.noctiva.noxway.wellness.domain.model.aggregates;

import static com.noctiva.noxway.shared.domain.Rules.require;

import com.noctiva.noxway.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * SleepGoal aggregate root capturing daily target sleep minutes for a worker.
 */
public class SleepGoal extends AbstractDomainAggregateRoot<SleepGoal> {

  private final UUID id;
  private final UUID workerId;
  private int targetMinutes;
  private final Instant createdAt;
  private final long version;

  private SleepGoal(UUID id, UUID workerId, int targetMinutes, Instant createdAt, long version) {
    this.id = id;
    this.workerId = workerId;
    this.targetMinutes = targetMinutes;
    this.createdAt = createdAt;
    this.version = version;
  }

  public static SleepGoal create(UUID workerId, int targetMinutes) {
    Objects.requireNonNull(workerId, "workerId is required");
    validateTarget(targetMinutes);
    return new SleepGoal(UUID.randomUUID(), workerId, targetMinutes, Instant.now(), 0L);
  }

  public static SleepGoal reconstitute(
      UUID id, UUID workerId, int targetMinutes, Instant createdAt, long version) {
    return new SleepGoal(id, workerId, targetMinutes, createdAt, version);
  }

  public void updateTarget(int targetMinutes) {
    validateTarget(targetMinutes);
    this.targetMinutes = targetMinutes;
  }

  private static void validateTarget(int minutes) {
    require(minutes >= 240 && minutes <= 720, "Daily sleep goal must be 240..720 minutes");
  }

  public UUID getId() {
    return id;
  }

  public UUID getWorkerId() {
    return workerId;
  }

  public int getTargetMinutes() {
    return targetMinutes;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public long getVersion() {
    return version;
  }
}
