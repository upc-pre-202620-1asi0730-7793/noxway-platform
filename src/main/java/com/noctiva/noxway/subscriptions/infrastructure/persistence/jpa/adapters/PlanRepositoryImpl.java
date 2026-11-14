package com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.adapters;

import com.noctiva.noxway.shared.infrastructure.events.DomainEventPublisher;
import com.noctiva.noxway.shared.infrastructure.persistence.jpa.adapters.OptimisticJpaSaveSupport;
import com.noctiva.noxway.subscriptions.domain.model.aggregates.Plan;
import com.noctiva.noxway.subscriptions.domain.repositories.PlanRepository;
import com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.assemblers.SubscriptionsPersistenceAssembler;
import com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.repositories.PlanPersistenceRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class PlanRepositoryImpl implements PlanRepository {

  private final PlanPersistenceRepository jpa;
  private final DomainEventPublisher events;

  public PlanRepositoryImpl(PlanPersistenceRepository jpa, DomainEventPublisher events) {
    this.jpa = jpa;
    this.events = events;
  }

  @Override
  public Optional<Plan> findById(UUID id) {
    return jpa.findById(id).map(SubscriptionsPersistenceAssembler::toDomain);
  }

  @Override
  public List<Plan> findAll() {
    return jpa.findAll().stream().map(SubscriptionsPersistenceAssembler::toDomain).toList();
  }

  @Override
  public Plan save(Plan aggregate) {
    return OptimisticJpaSaveSupport.save(
        jpa,
        aggregate.getId(),
        aggregate.getVersion(),
        aggregate,
        SubscriptionsPersistenceAssembler::toPersistence,
        SubscriptionsPersistenceAssembler::toDomain,
        events,
        Plan.class);
  }

  @Override
  public void delete(UUID id) {
    jpa.deleteById(id);
  }

  @Override
  public List<Plan> findByCode(String value) {
    return jpa.findByCode(value).stream().map(SubscriptionsPersistenceAssembler::toDomain).toList();
  }
}
