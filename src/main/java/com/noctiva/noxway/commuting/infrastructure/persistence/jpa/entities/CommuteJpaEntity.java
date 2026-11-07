package com.noctiva.noxway.commuting.infrastructure.persistence.jpa.entities;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "commutes")
public class CommuteJpaEntity {
  @Id
  @Column(name = "id", nullable = false)
  public UUID id;

  @Column(name = "worker_id", nullable = false)
  public UUID workerId;

  @Column(name = "origin", nullable = false, length = 500)
  public String origin;

  @Column(name = "destination", nullable = false, length = 500)
  public String destination;

  @Column(name = "transport", nullable = false, length = 50)
  public String transport;

  @Column(name = "start_time", nullable = false)
  public Instant startTime;

  @Column(name = "estimated_arrival_time", nullable = false)
  public Instant estimatedArrivalTime;

  @Column(name = "tolerance_minutes", nullable = false)
  public int toleranceMinutes;

  @Column(name = "status", nullable = false, length = 32)
  public String status;

  @Column(name = "arrival_time", nullable = true)
  public Instant arrivalTime;

  @Column(name = "delay_until", nullable = true)
  public Instant delayUntil;

  @Column(name = "created_at", nullable = false)
  public Instant createdAt;

  @Version public long version;
}
