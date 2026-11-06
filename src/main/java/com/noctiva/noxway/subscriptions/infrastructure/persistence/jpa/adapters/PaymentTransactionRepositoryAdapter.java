package com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.adapters;

import com.noctiva.noxway.subscriptions.domain.model.aggregates.PaymentTransaction;
import com.noctiva.noxway.subscriptions.domain.repositories.PaymentTransactionRepository;
import com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.entities.PaymentTransactionJpaEntity;
import com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.mappers.PaymentTransactionPersistenceMapper;
import com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.repositories.PaymentTransactionJpaRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class PaymentTransactionRepositoryAdapter implements PaymentTransactionRepository {
  private final PaymentTransactionJpaRepository jpa;

  public PaymentTransactionRepositoryAdapter(PaymentTransactionJpaRepository jpa) {
    this.jpa = jpa;
  }

  public Optional<PaymentTransaction> findById(UUID id) {
    return jpa.findById(id).map(PaymentTransactionPersistenceMapper::toDomain);
  }

  public java.util.List<PaymentTransaction> findAll() {
    return jpa.findAll().stream().map(PaymentTransactionPersistenceMapper::toDomain).toList();
  }

  public PaymentTransaction save(PaymentTransaction a) {
    var e = jpa.findById(a.id()).orElseGet(PaymentTransactionJpaEntity::new);
    if (e.id != null && e.version != a.version())
      throw new org.springframework.orm.ObjectOptimisticLockingFailureException(
          PaymentTransaction.class, a.id());
    PaymentTransactionPersistenceMapper.copy(a, e);
    return PaymentTransactionPersistenceMapper.toDomain(jpa.saveAndFlush(e));
  }

  public void delete(UUID id) {
    jpa.deleteById(id);
  }

  public java.util.List<PaymentTransaction> findBySubscriptionId(UUID value) {
    return jpa.findBySubscriptionId(value).stream()
        .map(PaymentTransactionPersistenceMapper::toDomain)
        .toList();
  }

  public java.util.List<PaymentTransaction> findByWorkerId(UUID value) {
    return jpa.findByWorkerId(value).stream()
        .map(PaymentTransactionPersistenceMapper::toDomain)
        .toList();
  }

  public java.util.List<PaymentTransaction> findByStatus(String value) {
    return jpa.findByStatus(value).stream()
        .map(PaymentTransactionPersistenceMapper::toDomain)
        .toList();
  }
}
