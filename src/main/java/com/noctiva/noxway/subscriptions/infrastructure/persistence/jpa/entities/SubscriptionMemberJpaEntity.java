package com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.entities;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "subscription_members")
public class SubscriptionMemberJpaEntity {
  @Id
  @Column(name = "id", nullable = false)
  public UUID id;

  @Column(name = "subscription_id", nullable = false)
  public UUID subscriptionId;

  @Column(name = "worker_id", nullable = false)
  public UUID workerId;

  @Column(name = "status", nullable = false, length = 32)
  public String status;

  @Column(name = "created_at", nullable = false)
  public Instant createdAt;

  @Version public long version;
}
