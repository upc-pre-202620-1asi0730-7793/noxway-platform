package com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.adapters;

import com.noctiva.noxway.subscriptions.domain.model.aggregates.Subscription;
import com.noctiva.noxway.subscriptions.domain.repositories.SubscriptionRepository;
import com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.entities.SubscriptionJpaEntity;
import com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.mappers.SubscriptionPersistenceMapper;
import com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.repositories.SubscriptionJpaRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class SubscriptionRepositoryAdapter implements SubscriptionRepository {
  private final SubscriptionJpaRepository jpa;

  public SubscriptionRepositoryAdapter(SubscriptionJpaRepository jpa) {
    this.jpa = jpa;
  }

  public Optional<Subscription> findById(UUID id) {
    return jpa.findById(id).map(SubscriptionPersistenceMapper::toDomain);
  }

  public java.util.List<Subscription> findAll() {
    return jpa.findAll().stream().map(SubscriptionPersistenceMapper::toDomain).toList();
  }

  public Subscription save(Subscription a) {
    var e = jpa.findById(a.id()).orElseGet(SubscriptionJpaEntity::new);
    if (e.id != null && e.version != a.version())
      throw new org.springframework.orm.ObjectOptimisticLockingFailureException(
          Subscription.class, a.id());
    SubscriptionPersistenceMapper.copy(a, e);
    return SubscriptionPersistenceMapper.toDomain(jpa.saveAndFlush(e));
  }

  public void delete(UUID id) {
    jpa.deleteById(id);
  }

  public java.util.List<Subscription> findByWorkerId(UUID value) {
    return jpa.findByWorkerId(value).stream().map(SubscriptionPersistenceMapper::toDomain).toList();
  }

  public java.util.List<Subscription> findByPlanId(UUID value) {
    return jpa.findByPlanId(value).stream().map(SubscriptionPersistenceMapper::toDomain).toList();
  }

  public java.util.List<Subscription> findByStatus(String value) {
    return jpa.findByStatus(value).stream().map(SubscriptionPersistenceMapper::toDomain).toList();
  }
}
