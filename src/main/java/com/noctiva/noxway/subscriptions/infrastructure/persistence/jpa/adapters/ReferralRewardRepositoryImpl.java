package com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.adapters;

import com.noctiva.noxway.shared.infrastructure.events.DomainEventPublisher;
import com.noctiva.noxway.shared.infrastructure.persistence.jpa.adapters.OptimisticJpaSaveSupport;
import com.noctiva.noxway.subscriptions.domain.model.aggregates.ReferralReward;
import com.noctiva.noxway.subscriptions.domain.repositories.ReferralRewardRepository;
import com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.assemblers.SubscriptionsPersistenceAssembler;
import com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.repositories.ReferralRewardPersistenceRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class ReferralRewardRepositoryImpl implements ReferralRewardRepository {

  private final ReferralRewardPersistenceRepository jpa;
  private final DomainEventPublisher events;

  public ReferralRewardRepositoryImpl(
      ReferralRewardPersistenceRepository jpa, DomainEventPublisher events) {
    this.jpa = jpa;
    this.events = events;
  }

  @Override
  public Optional<ReferralReward> findById(UUID id) {
    return jpa.findById(id).map(SubscriptionsPersistenceAssembler::toDomain);
  }

  @Override
  public List<ReferralReward> findAll() {
    return jpa.findAll().stream().map(SubscriptionsPersistenceAssembler::toDomain).toList();
  }

  @Override
  public ReferralReward save(ReferralReward aggregate) {
    return OptimisticJpaSaveSupport.save(
        jpa,
        aggregate.getId(),
        aggregate.getVersion(),
        aggregate,
        SubscriptionsPersistenceAssembler::toPersistence,
        SubscriptionsPersistenceAssembler::toDomain,
        events,
        ReferralReward.class);
  }

  @Override
  public void delete(UUID id) {
    jpa.deleteById(id);
  }

  @Override
  public List<ReferralReward> findByWorkerId(UUID value) {
    return jpa.findByWorkerId(value).stream()
        .map(SubscriptionsPersistenceAssembler::toDomain)
        .toList();
  }

  @Override
  public List<ReferralReward> findByReferralUseId(UUID value) {
    return jpa.findByReferralUseId(value).stream()
        .map(SubscriptionsPersistenceAssembler::toDomain)
        .toList();
  }

  @Override
  public List<ReferralReward> findByStatus(String value) {
    return jpa.findByStatus(value).stream().map(SubscriptionsPersistenceAssembler::toDomain).toList();
  }
}
