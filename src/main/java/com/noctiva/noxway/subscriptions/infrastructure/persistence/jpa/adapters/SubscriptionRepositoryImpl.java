package com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.adapters;

import com.noctiva.noxway.shared.infrastructure.events.DomainEventPublisher;
import com.noctiva.noxway.shared.infrastructure.persistence.jpa.adapters.OptimisticJpaSaveSupport;
import com.noctiva.noxway.subscriptions.domain.model.aggregates.Subscription;
import com.noctiva.noxway.subscriptions.domain.repositories.SubscriptionRepository;
import com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.assemblers.SubscriptionsPersistenceAssembler;
import com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.repositories.SubscriptionPersistenceRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class SubscriptionRepositoryImpl implements SubscriptionRepository {

  private final SubscriptionPersistenceRepository jpa;
  private final DomainEventPublisher events;

  public SubscriptionRepositoryImpl(
      SubscriptionPersistenceRepository jpa, DomainEventPublisher events) {
    this.jpa = jpa;
    this.events = events;
  }

  @Override
  public Optional<Subscription> findById(UUID id) {
    return jpa.findById(id).map(SubscriptionsPersistenceAssembler::toDomain);
  }

  @Override
  public List<Subscription> findAll() {
    return jpa.findAll().stream().map(SubscriptionsPersistenceAssembler::toDomain).toList();
  }

  @Override
  public Subscription save(Subscription aggregate) {
    return OptimisticJpaSaveSupport.save(
        jpa,
        aggregate.getId(),
        aggregate.getVersion(),
        aggregate,
        SubscriptionsPersistenceAssembler::toPersistence,
        SubscriptionsPersistenceAssembler::toDomain,
        events,
        Subscription.class);
  }

  @Override
  public void delete(UUID id) {
    jpa.deleteById(id);
  }

  @Override
  public List<Subscription> findByWorkerId(UUID value) {
    return jpa.findByWorkerId(value).stream()
        .map(SubscriptionsPersistenceAssembler::toDomain)
        .toList();
  }

  @Override
  public List<Subscription> findByPlanId(UUID value) {
    return jpa.findByPlanId(value).stream().map(SubscriptionsPersistenceAssembler::toDomain).toList();
  }

  @Override
  public List<Subscription> findByStatus(String value) {
    return jpa.findByStatus(value).stream().map(SubscriptionsPersistenceAssembler::toDomain).toList();
  }
}
