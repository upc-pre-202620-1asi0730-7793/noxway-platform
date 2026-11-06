package com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.repositories;

import com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.entities.PaymentTransactionJpaEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentTransactionJpaRepository
    extends JpaRepository<PaymentTransactionJpaEntity, UUID> {
  java.util.List<PaymentTransactionJpaEntity> findBySubscriptionId(UUID value);

  java.util.List<PaymentTransactionJpaEntity> findByWorkerId(UUID value);

  java.util.List<PaymentTransactionJpaEntity> findByStatus(String value);
}
