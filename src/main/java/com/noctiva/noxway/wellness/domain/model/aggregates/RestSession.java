package com.noctiva.noxway.wellness.domain.model.aggregates;

import static com.noctiva.noxway.shared.domain.Rules.require;

import com.noctiva.noxway.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import com.noctiva.noxway.wellness.domain.model.events.RestSessionLoggedEvent;
import com.noctiva.noxway.wellness.domain.model.valueobjects.RestRules;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * RestSession aggregate root representing a recorded daytime or nighttime sleep period.
 */
public class RestSession extends AbstractDomainAggregateRoot<RestSession> {

  private final UUID id;
  private final UUID workerId;
  private final Instant startTime;
  private final Instant endTime;
  private final int quality;
  private final String notes;
  private final Instant createdAt;
  private final long version;

  private RestSession(
      UUID id,
      UUID workerId,
      Instant startTime,
      Instant endTime,
      int quality,
      String notes,
      Instant createdAt,
      long version) {
    this.id = id;
    this.workerId = workerId;
    this.startTime = startTime;
    this.endTime = endTime;
    this.quality = quality;
    this.notes = notes;
    this.createdAt = createdAt;
    this.version = version;
  }

  public static RestSession create(
      UUID workerId, Instant startTime, Instant endTime, int quality, String notes) {
    Objects.requireNonNull(workerId, "workerId is required");
    long minutes = RestRules.minutes(startTime, endTime);
    require(quality >= 1 && quality <= 5, "Quality rating must be between 1 and 5");
    String cleanNotes = notes == null ? "" : notes.trim();
    var session =
        new RestSession(
            UUID.randomUUID(),
            workerId,
            startTime,
            endTime,
            quality,
            cleanNotes,
            Instant.now(),
            0L);
    session.registerEvent(
        new RestSessionLoggedEvent(session.id, workerId, minutes, quality, Instant.now()));
    return session;
  }

  public static RestSession reconstitute(
      UUID id,
      UUID workerId,
      Instant startTime,
      Instant endTime,
      int quality,
      String notes,
      Instant createdAt,
      long version) {
    return new RestSession(id, workerId, startTime, endTime, quality, notes, createdAt, version);
  }

  public long durationMinutes() {
    return java.time.Duration.between(startTime, endTime).toMinutes();
  }

  public boolean overlaps(Instant otherStart, Instant otherEnd) {
    return RestRules.overlaps(startTime, endTime, otherStart, otherEnd);
  }

  public UUID getId() {
    return id;
  }

  public UUID getWorkerId() {
    return workerId;
  }

  public Instant getStartTime() {
    return startTime;
  }

  public Instant getEndTime() {
    return endTime;
  }

  public int getQuality() {
    return quality;
  }

  public String getNotes() {
    return notes;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public long getVersion() {
    return version;
  }
}
