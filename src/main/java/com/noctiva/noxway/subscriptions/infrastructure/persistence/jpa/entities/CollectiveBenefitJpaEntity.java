package com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.entities;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "collective_benefits")
public class CollectiveBenefitJpaEntity {
  @Id
  @Column(name = "id", nullable = false)
  public UUID id;

  @Column(name = "name", nullable = false, length = 150)
  public String name;

  @Column(name = "description", nullable = false, length = 1500)
  public String description;

  @Column(name = "plan_code", nullable = false, length = 32)
  public String planCode;

  @Column(name = "active", nullable = false)
  public boolean active;

  @Column(name = "created_at", nullable = false)
  public Instant createdAt;

  @Version public long version;
}
