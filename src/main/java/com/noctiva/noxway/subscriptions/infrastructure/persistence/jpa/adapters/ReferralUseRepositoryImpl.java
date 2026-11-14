package com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.adapters;

import com.noctiva.noxway.shared.infrastructure.events.DomainEventPublisher;
import com.noctiva.noxway.shared.infrastructure.persistence.jpa.adapters.OptimisticJpaSaveSupport;
import com.noctiva.noxway.subscriptions.domain.model.aggregates.ReferralUse;
import com.noctiva.noxway.subscriptions.domain.repositories.ReferralUseRepository;
import com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.assemblers.SubscriptionsPersistenceAssembler;
import com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.repositories.ReferralUsePersistenceRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class ReferralUseRepositoryImpl implements ReferralUseRepository {

  private final ReferralUsePersistenceRepository jpa;
  private final DomainEventPublisher events;

  public ReferralUseRepositoryImpl(
      ReferralUsePersistenceRepository jpa, DomainEventPublisher events) {
    this.jpa = jpa;
    this.events = events;
  }

  @Override
  public Optional<ReferralUse> findById(UUID id) {
    return jpa.findById(id).map(SubscriptionsPersistenceAssembler::toDomain);
  }

  @Override
  public List<ReferralUse> findAll() {
    return jpa.findAll().stream().map(SubscriptionsPersistenceAssembler::toDomain).toList();
  }

  @Override
  public ReferralUse save(ReferralUse aggregate) {
    return OptimisticJpaSaveSupport.save(
        jpa,
        aggregate.getId(),
        aggregate.getVersion(),
        aggregate,
        SubscriptionsPersistenceAssembler::toPersistence,
        SubscriptionsPersistenceAssembler::toDomain,
        events,
        ReferralUse.class);
  }

  @Override
  public void delete(UUID id) {
    jpa.deleteById(id);
  }

  @Override
  public List<ReferralUse> findByReferralId(UUID value) {
    return jpa.findByReferralId(value).stream()
        .map(SubscriptionsPersistenceAssembler::toDomain)
        .toList();
  }

  @Override
  public List<ReferralUse> findByReferredWorkerId(UUID value) {
    return jpa.findByReferredWorkerId(value).stream()
        .map(SubscriptionsPersistenceAssembler::toDomain)
        .toList();
  }
}
