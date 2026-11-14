package com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.adapters;

import com.noctiva.noxway.shared.infrastructure.events.DomainEventPublisher;
import com.noctiva.noxway.shared.infrastructure.persistence.jpa.adapters.OptimisticJpaSaveSupport;
import com.noctiva.noxway.subscriptions.domain.model.aggregates.SubscriptionMember;
import com.noctiva.noxway.subscriptions.domain.repositories.SubscriptionMemberRepository;
import com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.assemblers.SubscriptionsPersistenceAssembler;
import com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.repositories.SubscriptionMemberPersistenceRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class SubscriptionMemberRepositoryImpl implements SubscriptionMemberRepository {

  private final SubscriptionMemberPersistenceRepository jpa;
  private final DomainEventPublisher events;

  public SubscriptionMemberRepositoryImpl(
      SubscriptionMemberPersistenceRepository jpa, DomainEventPublisher events) {
    this.jpa = jpa;
    this.events = events;
  }

  @Override
  public Optional<SubscriptionMember> findById(UUID id) {
    return jpa.findById(id).map(SubscriptionsPersistenceAssembler::toDomain);
  }

  @Override
  public List<SubscriptionMember> findAll() {
    return jpa.findAll().stream().map(SubscriptionsPersistenceAssembler::toDomain).toList();
  }

  @Override
  public SubscriptionMember save(SubscriptionMember aggregate) {
    return OptimisticJpaSaveSupport.save(
        jpa,
        aggregate.getId(),
        aggregate.getVersion(),
        aggregate,
        SubscriptionsPersistenceAssembler::toPersistence,
        SubscriptionsPersistenceAssembler::toDomain,
        events,
        SubscriptionMember.class);
  }

  @Override
  public void delete(UUID id) {
    jpa.deleteById(id);
  }

  @Override
  public List<SubscriptionMember> findBySubscriptionId(UUID value) {
    return jpa.findBySubscriptionId(value).stream()
        .map(SubscriptionsPersistenceAssembler::toDomain)
        .toList();
  }

  @Override
  public List<SubscriptionMember> findByWorkerId(UUID value) {
    return jpa.findByWorkerId(value).stream()
        .map(SubscriptionsPersistenceAssembler::toDomain)
        .toList();
  }

  @Override
  public List<SubscriptionMember> findByStatus(String value) {
    return jpa.findByStatus(value).stream().map(SubscriptionsPersistenceAssembler::toDomain).toList();
  }
}
