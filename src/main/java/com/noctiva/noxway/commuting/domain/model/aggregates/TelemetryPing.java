package com.noctiva.noxway.commuting.domain.model.aggregates;

import java.time.Instant;
import java.util.UUID;

public record TelemetryPing(
    UUID id,
    UUID commuteId,
    double latitude,
    double longitude,
    double speed,
    Instant recordedAt,
    Instant createdAt,
    long version) {
  public static TelemetryPing create(
      UUID commuteId, double latitude, double longitude, double speed, Instant recordedAt) {
    return new TelemetryPing(
        UUID.randomUUID(), commuteId, latitude, longitude, speed, recordedAt, Instant.now(), 0);
  }

  public TelemetryPing withCommuteId(UUID value) {
    return new TelemetryPing(id, value, latitude, longitude, speed, recordedAt, createdAt, version);
  }

  public TelemetryPing withLatitude(double value) {
    return new TelemetryPing(
        id, commuteId, value, longitude, speed, recordedAt, createdAt, version);
  }

  public TelemetryPing withLongitude(double value) {
    return new TelemetryPing(id, commuteId, latitude, value, speed, recordedAt, createdAt, version);
  }

  public TelemetryPing withSpeed(double value) {
    return new TelemetryPing(
        id, commuteId, latitude, longitude, value, recordedAt, createdAt, version);
  }

  public TelemetryPing withRecordedAt(Instant value) {
    return new TelemetryPing(id, commuteId, latitude, longitude, speed, value, createdAt, version);
  }
}
