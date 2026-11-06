package com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.entities;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "referral_uses", schema = "subscription_schema")
public class ReferralUseJpaEntity {
  @Id
  @Column(name = "id", nullable = false)
  public UUID id;

  @Column(name = "referral_id", nullable = false)
  public UUID referralId;

  @Column(name = "referred_worker_id", nullable = false)
  public UUID referredWorkerId;

  @Column(name = "created_at", nullable = false)
  public Instant createdAt;

  @Version public long version;
}
