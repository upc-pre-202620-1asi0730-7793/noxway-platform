package com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.adapters;

import com.noctiva.noxway.shared.infrastructure.events.DomainEventPublisher;
import com.noctiva.noxway.shared.infrastructure.persistence.jpa.adapters.OptimisticJpaSaveSupport;
import com.noctiva.noxway.subscriptions.domain.model.aggregates.CollectiveBenefit;
import com.noctiva.noxway.subscriptions.domain.repositories.CollectiveBenefitRepository;
import com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.assemblers.SubscriptionsPersistenceAssembler;
import com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.repositories.CollectiveBenefitPersistenceRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class CollectiveBenefitRepositoryImpl implements CollectiveBenefitRepository {

  private final CollectiveBenefitPersistenceRepository jpa;
  private final DomainEventPublisher events;

  public CollectiveBenefitRepositoryImpl(
      CollectiveBenefitPersistenceRepository jpa, DomainEventPublisher events) {
    this.jpa = jpa;
    this.events = events;
  }

  @Override
  public Optional<CollectiveBenefit> findById(UUID id) {
    return jpa.findById(id).map(SubscriptionsPersistenceAssembler::toDomain);
  }

  @Override
  public List<CollectiveBenefit> findAll() {
    return jpa.findAll().stream().map(SubscriptionsPersistenceAssembler::toDomain).toList();
  }

  @Override
  public CollectiveBenefit save(CollectiveBenefit aggregate) {
    return OptimisticJpaSaveSupport.save(
        jpa,
        aggregate.getId(),
        aggregate.getVersion(),
        aggregate,
        SubscriptionsPersistenceAssembler::toPersistence,
        SubscriptionsPersistenceAssembler::toDomain,
        events,
        CollectiveBenefit.class);
  }

  @Override
  public void delete(UUID id) {
    jpa.deleteById(id);
  }
}
