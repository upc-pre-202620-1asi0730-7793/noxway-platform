package com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.entities;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "benefit_claims", schema = "subscription_schema")
public class BenefitClaimJpaEntity {
  @Id
  @Column(name = "id", nullable = false)
  public UUID id;

  @Column(name = "worker_id", nullable = false)
  public UUID workerId;

  @Column(name = "benefit_id", nullable = false)
  public UUID benefitId;

  @Column(name = "code", nullable = false)
  public String code;

  @Column(name = "created_at", nullable = false)
  public Instant createdAt;

  @Version public long version;
}
