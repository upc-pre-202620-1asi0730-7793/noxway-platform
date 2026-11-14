package com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.repositories;

import com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.entities.PaymentTransactionPersistenceEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentTransactionPersistenceRepository
    extends JpaRepository<PaymentTransactionPersistenceEntity, UUID> {
  List<PaymentTransactionPersistenceEntity> findBySubscriptionId(UUID value);

  List<PaymentTransactionPersistenceEntity> findByWorkerId(UUID value);

  List<PaymentTransactionPersistenceEntity> findByStatus(String value);
}
