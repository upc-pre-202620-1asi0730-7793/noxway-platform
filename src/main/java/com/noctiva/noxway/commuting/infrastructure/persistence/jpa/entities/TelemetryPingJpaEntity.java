package com.noctiva.noxway.commuting.infrastructure.persistence.jpa.entities;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "telemetry_pings", schema = "commute_incident_schema")
public class TelemetryPingJpaEntity {
  @Id
  @Column(name = "id", nullable = false)
  public UUID id;

  @Column(name = "commute_id", nullable = false)
  public UUID commuteId;

  @Column(name = "latitude", nullable = false)
  public double latitude;

  @Column(name = "longitude", nullable = false)
  public double longitude;

  @Column(name = "speed", nullable = false)
  public double speed;

  @Column(name = "recorded_at", nullable = false)
  public Instant recordedAt;

  @Column(name = "created_at", nullable = false)
  public Instant createdAt;

  @Version public long version;
}
