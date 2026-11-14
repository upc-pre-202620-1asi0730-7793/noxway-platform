package com.noctiva.noxway.commuting.domain.model.aggregates;

import static com.noctiva.noxway.shared.domain.Rules.coordinates;
import static com.noctiva.noxway.shared.domain.Rules.require;

import com.noctiva.noxway.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class TelemetryPing extends AbstractDomainAggregateRoot<TelemetryPing> {

  private final UUID id;
  private final UUID commuteId;
  private final double latitude;
  private final double longitude;
  private final double speed;
  private final Instant recordedAt;
  private final Instant createdAt;
  private final long version;

  private TelemetryPing(
      UUID id,
      UUID commuteId,
      double latitude,
      double longitude,
      double speed,
      Instant recordedAt,
      Instant createdAt,
      long version) {
    this.id = id;
    this.commuteId = commuteId;
    this.latitude = latitude;
    this.longitude = longitude;
    this.speed = speed;
    this.recordedAt = recordedAt;
    this.createdAt = createdAt;
    this.version = version;
  }

  public static TelemetryPing create(
      UUID commuteId, double latitude, double longitude, double speed, Instant recordedAt) {
    Objects.requireNonNull(commuteId);
    Objects.requireNonNull(recordedAt);
    coordinates(latitude, longitude);
    require(speed >= 0 && speed <= 300, "Speed must be 0..300 km/h");
    return new TelemetryPing(
        UUID.randomUUID(), commuteId, latitude, longitude, speed, recordedAt, Instant.now(), 0L);
  }

  public static TelemetryPing reconstitute(
      UUID id,
      UUID commuteId,
      double latitude,
      double longitude,
      double speed,
      Instant recordedAt,
      Instant createdAt,
      long version) {
    return new TelemetryPing(
        id, commuteId, latitude, longitude, speed, recordedAt, createdAt, version);
  }

  public UUID getId() {
    return id;
  }

  public UUID getCommuteId() {
    return commuteId;
  }

  public double getLatitude() {
    return latitude;
  }

  public double getLongitude() {
    return longitude;
  }

  public double getSpeed() {
    return speed;
  }

  public Instant getRecordedAt() {
    return recordedAt;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public long getVersion() {
    return version;
  }
}
