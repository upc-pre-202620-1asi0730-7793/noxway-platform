package com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.adapters;

import com.noctiva.noxway.subscriptions.domain.model.aggregates.ReferralUse;
import com.noctiva.noxway.subscriptions.domain.repositories.ReferralUseRepository;
import com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.entities.ReferralUseJpaEntity;
import com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.mappers.ReferralUsePersistenceMapper;
import com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.repositories.ReferralUseJpaRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class ReferralUseRepositoryAdapter implements ReferralUseRepository {
  private final ReferralUseJpaRepository jpa;

  public ReferralUseRepositoryAdapter(ReferralUseJpaRepository jpa) {
    this.jpa = jpa;
  }

  public Optional<ReferralUse> findById(UUID id) {
    return jpa.findById(id).map(ReferralUsePersistenceMapper::toDomain);
  }

  public java.util.List<ReferralUse> findAll() {
    return jpa.findAll().stream().map(ReferralUsePersistenceMapper::toDomain).toList();
  }

  public ReferralUse save(ReferralUse a) {
    var e = jpa.findById(a.id()).orElseGet(ReferralUseJpaEntity::new);
    if (e.id != null && e.version != a.version())
      throw new org.springframework.orm.ObjectOptimisticLockingFailureException(
          ReferralUse.class, a.id());
    ReferralUsePersistenceMapper.copy(a, e);
    return ReferralUsePersistenceMapper.toDomain(jpa.saveAndFlush(e));
  }

  public void delete(UUID id) {
    jpa.deleteById(id);
  }

  public java.util.List<ReferralUse> findByReferralId(UUID value) {
    return jpa.findByReferralId(value).stream()
        .map(ReferralUsePersistenceMapper::toDomain)
        .toList();
  }

  public java.util.List<ReferralUse> findByReferredWorkerId(UUID value) {
    return jpa.findByReferredWorkerId(value).stream()
        .map(ReferralUsePersistenceMapper::toDomain)
        .toList();
  }
}
