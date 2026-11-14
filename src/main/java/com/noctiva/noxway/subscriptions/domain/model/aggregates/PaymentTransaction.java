package com.noctiva.noxway.subscriptions.domain.model.aggregates;

import static com.noctiva.noxway.shared.domain.Rules.require;

import com.noctiva.noxway.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class PaymentTransaction extends AbstractDomainAggregateRoot<PaymentTransaction> {

  private final UUID id;
  private final UUID subscriptionId;
  private final UUID workerId;
  private final BigDecimal amount;
  private final String currency;
  private String reference;
  private String status;
  private final Instant createdAt;
  private final long version;

  private PaymentTransaction(
      UUID id,
      UUID subscriptionId,
      UUID workerId,
      BigDecimal amount,
      String currency,
      String reference,
      String status,
      Instant createdAt,
      long version) {
    this.id = id;
    this.subscriptionId = subscriptionId;
    this.workerId = workerId;
    this.amount = amount;
    this.currency = currency;
    this.reference = reference;
    this.status = status;
    this.createdAt = createdAt;
    this.version = version;
  }

  public static PaymentTransaction createPending(
      UUID subscriptionId, UUID workerId, BigDecimal amount, String currency) {
    require(amount != null && amount.signum() > 0, "Payment amount must be positive");
    UUID id = UUID.randomUUID();
    return new PaymentTransaction(
        id, subscriptionId, workerId, amount, currency, "PENDING-" + id, "PENDING", Instant.now(), 0L);
  }

  public static PaymentTransaction reconstitute(
      UUID id,
      UUID subscriptionId,
      UUID workerId,
      BigDecimal amount,
      String currency,
      String reference,
      String status,
      Instant createdAt,
      long version) {
    return new PaymentTransaction(
        id, subscriptionId, workerId, amount, currency, reference, status, createdAt, version);
  }

  public void confirmOffline(String receiptReference) {
    require("PENDING".equals(status), "Payment already resolved");
    require(receiptReference != null && !receiptReference.isBlank(), "Receipt reference required");
    this.reference = receiptReference.trim();
    this.status = "CONFIRMED";
  }

  public void reject() {
    require("PENDING".equals(status), "Payment already resolved");
    this.status = "REJECTED";
  }

  public UUID getId() {
    return id;
  }

  public UUID getSubscriptionId() {
    return subscriptionId;
  }

  public UUID getWorkerId() {
    return workerId;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public String getCurrency() {
    return currency;
  }

  public String getReference() {
    return reference;
  }

  public String getStatus() {
    return status;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public long getVersion() {
    return version;
  }
}
