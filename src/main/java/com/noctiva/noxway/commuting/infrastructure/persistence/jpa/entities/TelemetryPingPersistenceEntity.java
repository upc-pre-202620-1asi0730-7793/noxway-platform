package com.noctiva.noxway.commuting.infrastructure.persistence.jpa.entities;

import com.noctiva.noxway.shared.infrastructure.persistence.jpa.entities.AuditableUuidPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "telemetry_pings", schema = "commute_incident_schema")
public class TelemetryPingPersistenceEntity extends AuditableUuidPersistenceEntity {

  @Column(name = "commute_id", nullable = false)
  private UUID commuteId;

  @Column(name = "latitude", nullable = false)
  private double latitude;

  @Column(name = "longitude", nullable = false)
  private double longitude;

  @Column(name = "speed", nullable = false)
  private double speed;

  @Column(name = "recorded_at", nullable = false)
  private Instant recordedAt;

  public TelemetryPingPersistenceEntity() {}

  public UUID getCommuteId() {
    return commuteId;
  }

  public void setCommuteId(UUID commuteId) {
    this.commuteId = commuteId;
  }

  public double getLatitude() {
    return latitude;
  }

  public void setLatitude(double latitude) {
    this.latitude = latitude;
  }

  public double getLongitude() {
    return longitude;
  }

  public void setLongitude(double longitude) {
    this.longitude = longitude;
  }

  public double getSpeed() {
    return speed;
  }

  public void setSpeed(double speed) {
    this.speed = speed;
  }

  public Instant getRecordedAt() {
    return recordedAt;
  }

  public void setRecordedAt(Instant recordedAt) {
    this.recordedAt = recordedAt;
  }
}
