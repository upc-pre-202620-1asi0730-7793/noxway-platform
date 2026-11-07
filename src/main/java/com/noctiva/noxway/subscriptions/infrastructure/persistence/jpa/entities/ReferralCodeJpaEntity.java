package com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.entities;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "referral_codes")
public class ReferralCodeJpaEntity {
  @Id
  @Column(name = "id", nullable = false)
  public UUID id;

  @Column(name = "worker_id", nullable = false)
  public UUID workerId;

  @Column(name = "code", nullable = false, unique = true, length = 100)
  public String code;

  @Column(name = "created_at", nullable = false)
  public Instant createdAt;

  @Version public long version;
}
