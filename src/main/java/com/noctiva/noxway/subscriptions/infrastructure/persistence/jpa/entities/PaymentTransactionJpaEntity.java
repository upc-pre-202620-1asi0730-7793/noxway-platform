package com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "payment_transactions", schema = "subscription_schema")
public class PaymentTransactionJpaEntity {
  @Id
  @Column(name = "id", nullable = false)
  public UUID id;

  @Column(name = "subscription_id", nullable = false)
  public UUID subscriptionId;

  @Column(name = "worker_id", nullable = false)
  public UUID workerId;

  @Column(name = "amount", nullable = false, precision = 12, scale = 2)
  public BigDecimal amount;

  @Column(name = "currency", nullable = false)
  public String currency;

  @Column(name = "reference", nullable = false)
  public String reference;

  @Column(name = "status", nullable = false)
  public String status;

  @Column(name = "created_at", nullable = false)
  public Instant createdAt;

  @Version public long version;
}
