package com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.adapters;

import com.noctiva.noxway.shared.infrastructure.events.DomainEventPublisher;
import com.noctiva.noxway.shared.infrastructure.persistence.jpa.adapters.OptimisticJpaSaveSupport;
import com.noctiva.noxway.subscriptions.domain.model.aggregates.ReferralCode;
import com.noctiva.noxway.subscriptions.domain.repositories.ReferralCodeRepository;
import com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.assemblers.SubscriptionsPersistenceAssembler;
import com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.repositories.ReferralCodePersistenceRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class ReferralCodeRepositoryImpl implements ReferralCodeRepository {

  private final ReferralCodePersistenceRepository jpa;
  private final DomainEventPublisher events;

  public ReferralCodeRepositoryImpl(
      ReferralCodePersistenceRepository jpa, DomainEventPublisher events) {
    this.jpa = jpa;
    this.events = events;
  }

  @Override
  public Optional<ReferralCode> findById(UUID id) {
    return jpa.findById(id).map(SubscriptionsPersistenceAssembler::toDomain);
  }

  @Override
  public List<ReferralCode> findAll() {
    return jpa.findAll().stream().map(SubscriptionsPersistenceAssembler::toDomain).toList();
  }

  @Override
  public ReferralCode save(ReferralCode aggregate) {
    return OptimisticJpaSaveSupport.save(
        jpa,
        aggregate.getId(),
        aggregate.getVersion(),
        aggregate,
        SubscriptionsPersistenceAssembler::toPersistence,
        SubscriptionsPersistenceAssembler::toDomain,
        events,
        ReferralCode.class);
  }

  @Override
  public void delete(UUID id) {
    jpa.deleteById(id);
  }

  @Override
  public List<ReferralCode> findByWorkerId(UUID value) {
    return jpa.findByWorkerId(value).stream().map(SubscriptionsPersistenceAssembler::toDomain).toList();
  }

  @Override
  public List<ReferralCode> findByCode(String value) {
    return jpa.findByCode(value).stream().map(SubscriptionsPersistenceAssembler::toDomain).toList();
  }
}
