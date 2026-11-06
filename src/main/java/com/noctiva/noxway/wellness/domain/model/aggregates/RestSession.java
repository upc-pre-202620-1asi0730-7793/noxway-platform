package com.noctiva.noxway.wellness.domain.model.aggregates;

import java.time.Instant;
import java.util.UUID;

public record RestSession(
    UUID id,
    UUID workerId,
    Instant startTime,
    Instant endTime,
    int quality,
    String notes,
    Instant createdAt,
    long version) {
  public static RestSession create(
      UUID workerId, Instant startTime, Instant endTime, int quality, String notes) {
    return new RestSession(
        UUID.randomUUID(), workerId, startTime, endTime, quality, notes, Instant.now(), 0);
  }

  public RestSession withWorkerId(UUID value) {
    return new RestSession(id, value, startTime, endTime, quality, notes, createdAt, version);
  }

  public RestSession withStartTime(Instant value) {
    return new RestSession(id, workerId, value, endTime, quality, notes, createdAt, version);
  }

  public RestSession withEndTime(Instant value) {
    return new RestSession(id, workerId, startTime, value, quality, notes, createdAt, version);
  }

  public RestSession withQuality(int value) {
    return new RestSession(id, workerId, startTime, endTime, value, notes, createdAt, version);
  }

  public RestSession withNotes(String value) {
    return new RestSession(id, workerId, startTime, endTime, quality, value, createdAt, version);
  }
}
