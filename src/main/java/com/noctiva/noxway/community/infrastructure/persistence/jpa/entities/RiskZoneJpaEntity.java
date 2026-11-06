package com.noctiva.noxway.community.infrastructure.persistence.jpa.entities;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "risk_zones", schema = "community_data_schema")
public class RiskZoneJpaEntity {
  @Id
  @Column(name = "id", nullable = false)
  public UUID id;

  @Column(name = "reported_by", nullable = false)
  public UUID reportedBy;

  @Column(name = "description", nullable = false)
  public String description;

  @Column(name = "risk_level", nullable = false)
  public String riskLevel;

  @Column(name = "latitude", nullable = false)
  public double latitude;

  @Column(name = "longitude", nullable = false)
  public double longitude;

  @Column(name = "status", nullable = false)
  public String status;

  @Column(name = "created_at", nullable = false)
  public Instant createdAt;

  @Version public long version;
}
