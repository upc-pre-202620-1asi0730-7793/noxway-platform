package com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.adapters;

import com.noctiva.noxway.shared.infrastructure.events.DomainEventPublisher;
import com.noctiva.noxway.shared.infrastructure.persistence.jpa.adapters.OptimisticJpaSaveSupport;
import com.noctiva.noxway.subscriptions.domain.model.aggregates.BenefitClaim;
import com.noctiva.noxway.subscriptions.domain.repositories.BenefitClaimRepository;
import com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.assemblers.SubscriptionsPersistenceAssembler;
import com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.repositories.BenefitClaimPersistenceRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class BenefitClaimRepositoryImpl implements BenefitClaimRepository {

  private final BenefitClaimPersistenceRepository jpa;
  private final DomainEventPublisher events;

  public BenefitClaimRepositoryImpl(
      BenefitClaimPersistenceRepository jpa, DomainEventPublisher events) {
    this.jpa = jpa;
    this.events = events;
  }

  @Override
  public Optional<BenefitClaim> findById(UUID id) {
    return jpa.findById(id).map(SubscriptionsPersistenceAssembler::toDomain);
  }

  @Override
  public List<BenefitClaim> findAll() {
    return jpa.findAll().stream().map(SubscriptionsPersistenceAssembler::toDomain).toList();
  }

  @Override
  public BenefitClaim save(BenefitClaim aggregate) {
    return OptimisticJpaSaveSupport.save(
        jpa,
        aggregate.getId(),
        aggregate.getVersion(),
        aggregate,
        SubscriptionsPersistenceAssembler::toPersistence,
        SubscriptionsPersistenceAssembler::toDomain,
        events,
        BenefitClaim.class);
  }

  @Override
  public void delete(UUID id) {
    jpa.deleteById(id);
  }

  @Override
  public List<BenefitClaim> findByWorkerId(UUID value) {
    return jpa.findByWorkerId(value).stream().map(SubscriptionsPersistenceAssembler::toDomain).toList();
  }

  @Override
  public List<BenefitClaim> findByBenefitId(UUID value) {
    return jpa.findByBenefitId(value).stream()
        .map(SubscriptionsPersistenceAssembler::toDomain)
        .toList();
  }

  @Override
  public List<BenefitClaim> findByCode(String value) {
    return jpa.findByCode(value).stream().map(SubscriptionsPersistenceAssembler::toDomain).toList();
  }
}
