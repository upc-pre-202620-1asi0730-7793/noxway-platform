package com.noctiva.noxway.community.infrastructure.persistence.jpa.entities;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "service_points", schema = "community_data_schema")
public class ServicePointJpaEntity {
  @Id
  @Column(name = "id", nullable = false)
  public UUID id;

  @Column(name = "reported_by", nullable = false)
  public UUID reportedBy;

  @Column(name = "name", nullable = false)
  public String name;

  @Column(name = "type", nullable = false)
  public String type;

  @Column(name = "latitude", nullable = false)
  public double latitude;

  @Column(name = "longitude", nullable = false)
  public double longitude;

  @Column(name = "time_zone", nullable = false)
  public String timeZone;

  @Column(name = "status", nullable = false)
  public String status;

  @Column(name = "created_at", nullable = false)
  public Instant createdAt;

  @Version public long version;
}
