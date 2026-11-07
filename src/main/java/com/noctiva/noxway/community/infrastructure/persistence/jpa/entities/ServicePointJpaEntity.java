package com.noctiva.noxway.community.infrastructure.persistence.jpa.entities;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "service_points")
public class ServicePointJpaEntity {
  @Id
  @Column(name = "id", nullable = false)
  public UUID id;

  @Column(name = "reported_by", nullable = false)
  public UUID reportedBy;

  @Column(name = "name", nullable = false, length = 150)
  public String name;

  @Column(name = "type", nullable = false, length = 32)
  public String type;

  @Column(name = "latitude", nullable = false)
  public double latitude;

  @Column(name = "longitude", nullable = false)
  public double longitude;

  @Column(name = "time_zone", nullable = false, length = 100)
  public String timeZone;

  @Column(name = "status", nullable = false, length = 32)
  public String status;

  @Column(name = "created_at", nullable = false)
  public Instant createdAt;

  @Version public long version;
}
