package com.noctiva.noxway.community.infrastructure.persistence.jpa.entities;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "service_opening_intervals", schema = "community_data_schema")
public class ServiceOpeningIntervalJpaEntity {
  @Id
  @Column(name = "id", nullable = false)
  public UUID id;

  @Column(name = "service_id", nullable = false)
  public UUID serviceId;

  @Column(name = "day_of_week", nullable = false)
  public int dayOfWeek;

  @Column(name = "opens_at", nullable = false)
  public String opensAt;

  @Column(name = "closes_at", nullable = false)
  public String closesAt;

  @Column(name = "closes_next_day", nullable = false)
  public boolean closesNextDay;

  @Column(name = "is24_hours", nullable = false)
  public boolean is24Hours;

  @Column(name = "created_at", nullable = false)
  public Instant createdAt;

  @Version public long version;
}
