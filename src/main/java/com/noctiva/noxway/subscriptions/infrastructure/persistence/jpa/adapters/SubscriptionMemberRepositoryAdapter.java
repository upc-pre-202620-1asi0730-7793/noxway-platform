package com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.adapters;

import com.noctiva.noxway.subscriptions.domain.model.aggregates.SubscriptionMember;
import com.noctiva.noxway.subscriptions.domain.repositories.SubscriptionMemberRepository;
import com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.entities.SubscriptionMemberJpaEntity;
import com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.mappers.SubscriptionMemberPersistenceMapper;
import com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.repositories.SubscriptionMemberJpaRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class SubscriptionMemberRepositoryAdapter implements SubscriptionMemberRepository {
  private final SubscriptionMemberJpaRepository jpa;

  public SubscriptionMemberRepositoryAdapter(SubscriptionMemberJpaRepository jpa) {
    this.jpa = jpa;
  }

  public Optional<SubscriptionMember> findById(UUID id) {
    return jpa.findById(id).map(SubscriptionMemberPersistenceMapper::toDomain);
  }

  public java.util.List<SubscriptionMember> findAll() {
    return jpa.findAll().stream().map(SubscriptionMemberPersistenceMapper::toDomain).toList();
  }

  public SubscriptionMember save(SubscriptionMember a) {
    var e = jpa.findById(a.id()).orElseGet(SubscriptionMemberJpaEntity::new);
    if (e.id != null && e.version != a.version())
      throw new org.springframework.orm.ObjectOptimisticLockingFailureException(
          SubscriptionMember.class, a.id());
    SubscriptionMemberPersistenceMapper.copy(a, e);
    return SubscriptionMemberPersistenceMapper.toDomain(jpa.saveAndFlush(e));
  }

  public void delete(UUID id) {
    jpa.deleteById(id);
  }

  public java.util.List<SubscriptionMember> findBySubscriptionId(UUID value) {
    return jpa.findBySubscriptionId(value).stream()
        .map(SubscriptionMemberPersistenceMapper::toDomain)
        .toList();
  }

  public java.util.List<SubscriptionMember> findByWorkerId(UUID value) {
    return jpa.findByWorkerId(value).stream()
        .map(SubscriptionMemberPersistenceMapper::toDomain)
        .toList();
  }

  public java.util.List<SubscriptionMember> findByStatus(String value) {
    return jpa.findByStatus(value).stream()
        .map(SubscriptionMemberPersistenceMapper::toDomain)
        .toList();
  }
}
