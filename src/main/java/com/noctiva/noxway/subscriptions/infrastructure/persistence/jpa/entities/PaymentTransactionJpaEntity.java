package com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "payment_transactions")
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

  @Column(name = "currency", nullable = false, length = 3)
  public String currency;

  @Column(name = "reference", nullable = false, length = 150)
  public String reference;

  @Column(name = "status", nullable = false, length = 32)
  public String status;

  @Column(name = "created_at", nullable = false)
  public Instant createdAt;

  @Version public long version;
}
