package com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.entities;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "collective_benefits", schema = "subscription_schema")
public class CollectiveBenefitJpaEntity {
  @Id
  @Column(name = "id", nullable = false)
  public UUID id;

  @Column(name = "name", nullable = false)
  public String name;

  @Column(name = "description", nullable = false)
  public String description;

  @Column(name = "plan_code", nullable = false)
  public String planCode;

  @Column(name = "active", nullable = false)
  public boolean active;

  @Column(name = "created_at", nullable = false)
  public Instant createdAt;

  @Version public long version;
}
