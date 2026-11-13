package com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.entities;

import com.noctiva.noxway.shared.infrastructure.persistence.jpa.entities.AuditableUuidPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "payment_transactions", schema = "subscription_schema")
public class PaymentTransactionPersistenceEntity extends AuditableUuidPersistenceEntity {

  @Column(name = "subscription_id", nullable = false)
  private UUID subscriptionId;

  @Column(name = "worker_id", nullable = false)
  private UUID workerId;

  @Column(name = "amount", nullable = false, precision = 12, scale = 2)
  private BigDecimal amount;

  @Column(name = "currency", nullable = false)
  private String currency;

  @Column(name = "reference", nullable = false)
  private String reference;

  @Column(name = "status", nullable = false)
  private String status;

  public PaymentTransactionPersistenceEntity() {}

  public UUID getSubscriptionId() {
    return subscriptionId;
  }

  public void setSubscriptionId(UUID subscriptionId) {
    this.subscriptionId = subscriptionId;
  }

  public UUID getWorkerId() {
    return workerId;
  }

  public void setWorkerId(UUID workerId) {
    this.workerId = workerId;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public String getReference() {
    return reference;
  }

  public void setReference(String reference) {
    this.reference = reference;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }
}
