package com.noctiva.noxway.wellness.infrastructure.persistence.jpa.entities;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "rest_sessions", schema = "wellness_schema")
public class RestSessionJpaEntity {
  @Id
  @Column(name = "id", nullable = false)
  public UUID id;

  @Column(name = "worker_id", nullable = false)
  public UUID workerId;

  @Column(name = "start_time", nullable = false)
  public Instant startTime;

  @Column(name = "end_time", nullable = false)
  public Instant endTime;

  @Column(name = "quality", nullable = false)
  public int quality;

  @Column(name = "notes", nullable = false)
  public String notes;

  @Column(name = "created_at", nullable = false)
  public Instant createdAt;

  @Version public long version;
}
