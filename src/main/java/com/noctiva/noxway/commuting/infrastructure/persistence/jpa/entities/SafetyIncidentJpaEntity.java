package com.noctiva.noxway.commuting.infrastructure.persistence.jpa.entities;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "safety_incidents", schema = "commute_incident_schema")
public class SafetyIncidentJpaEntity {
  @Id
  @Column(name = "id", nullable = false)
  public UUID id;

  @Column(name = "commute_id", nullable = false)
  public UUID commuteId;

  @Column(name = "worker_id", nullable = false)
  public UUID workerId;

  @Column(name = "type", nullable = false)
  public String type;

  @Column(name = "description", nullable = false)
  public String description;

  @Column(name = "status", nullable = false)
  public String status;

  @Column(name = "reported_at", nullable = false)
  public Instant reportedAt;

  @Column(name = "created_at", nullable = false)
  public Instant createdAt;

  @Version public long version;
}
