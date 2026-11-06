package com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.mappers;

import com.noctiva.noxway.subscriptions.domain.model.aggregates.PaymentTransaction;
import com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.entities.PaymentTransactionJpaEntity;

public final class PaymentTransactionPersistenceMapper {
  private PaymentTransactionPersistenceMapper() {}

  public static PaymentTransaction toDomain(PaymentTransactionJpaEntity e) {
    return new PaymentTransaction(
        e.id,
        e.subscriptionId,
        e.workerId,
        e.amount,
        e.currency,
        e.reference,
        e.status,
        e.createdAt,
        e.version);
  }

  public static void copy(PaymentTransaction a, PaymentTransactionJpaEntity e) {
    e.id = a.id();
    e.subscriptionId = a.subscriptionId();
    e.workerId = a.workerId();
    e.amount = a.amount();
    e.currency = a.currency();
    e.reference = a.reference();
    e.status = a.status();
    e.createdAt = a.createdAt();
  }
}
