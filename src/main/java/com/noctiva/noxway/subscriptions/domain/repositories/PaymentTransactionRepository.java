package com.noctiva.noxway.subscriptions.domain.repositories;

import com.noctiva.noxway.shared.domain.DomainRepository;
import com.noctiva.noxway.subscriptions.domain.model.aggregates.PaymentTransaction;
import java.util.UUID;

public interface PaymentTransactionRepository extends DomainRepository<PaymentTransaction> {
  java.util.List<PaymentTransaction> findBySubscriptionId(UUID value);

  java.util.List<PaymentTransaction> findByWorkerId(UUID value);

  java.util.List<PaymentTransaction> findByStatus(String value);
}
