package com.noctiva.noxway.subscriptions.domain.model.aggregates;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PaymentTransaction(
    UUID id,
    UUID subscriptionId,
    UUID workerId,
    BigDecimal amount,
    String currency,
    String reference,
    String status,
    Instant createdAt,
    long version) {
  public static PaymentTransaction create(
      UUID subscriptionId,
      UUID workerId,
      BigDecimal amount,
      String currency,
      String reference,
      String status) {
    return new PaymentTransaction(
        UUID.randomUUID(),
        subscriptionId,
        workerId,
        amount,
        currency,
        reference,
        status,
        Instant.now(),
        0);
  }

  public PaymentTransaction withSubscriptionId(UUID value) {
    return new PaymentTransaction(
        id, value, workerId, amount, currency, reference, status, createdAt, version);
  }

  public PaymentTransaction withWorkerId(UUID value) {
    return new PaymentTransaction(
        id, subscriptionId, value, amount, currency, reference, status, createdAt, version);
  }

  public PaymentTransaction withAmount(BigDecimal value) {
    return new PaymentTransaction(
        id, subscriptionId, workerId, value, currency, reference, status, createdAt, version);
  }

  public PaymentTransaction withCurrency(String value) {
    return new PaymentTransaction(
        id, subscriptionId, workerId, amount, value, reference, status, createdAt, version);
  }

  public PaymentTransaction withReference(String value) {
    return new PaymentTransaction(
        id, subscriptionId, workerId, amount, currency, value, status, createdAt, version);
  }

  public PaymentTransaction withStatus(String value) {
    return new PaymentTransaction(
        id, subscriptionId, workerId, amount, currency, reference, value, createdAt, version);
  }
}
