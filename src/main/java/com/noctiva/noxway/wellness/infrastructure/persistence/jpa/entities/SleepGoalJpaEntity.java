package com.noctiva.noxway.wellness.infrastructure.persistence.jpa.entities;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "sleep_goals")
public class SleepGoalJpaEntity {
  @Id
  @Column(name = "id", nullable = false)
  public UUID id;

  @Column(name = "worker_id", nullable = false)
  public UUID workerId;

  @Column(name = "target_minutes", nullable = false)
  public int targetMinutes;

  @Column(name = "created_at", nullable = false)
  public Instant createdAt;

  @Version public long version;
}
