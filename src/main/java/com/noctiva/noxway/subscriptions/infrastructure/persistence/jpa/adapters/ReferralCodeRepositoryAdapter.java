package com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.adapters;

import com.noctiva.noxway.subscriptions.domain.model.aggregates.ReferralCode;
import com.noctiva.noxway.subscriptions.domain.repositories.ReferralCodeRepository;
import com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.entities.ReferralCodeJpaEntity;
import com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.mappers.ReferralCodePersistenceMapper;
import com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.repositories.ReferralCodeJpaRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class ReferralCodeRepositoryAdapter implements ReferralCodeRepository {
  private final ReferralCodeJpaRepository jpa;

  public ReferralCodeRepositoryAdapter(ReferralCodeJpaRepository jpa) {
    this.jpa = jpa;
  }

  public Optional<ReferralCode> findById(UUID id) {
    return jpa.findById(id).map(ReferralCodePersistenceMapper::toDomain);
  }

  public java.util.List<ReferralCode> findAll() {
    return jpa.findAll().stream().map(ReferralCodePersistenceMapper::toDomain).toList();
  }

  public ReferralCode save(ReferralCode a) {
    var e = jpa.findById(a.id()).orElseGet(ReferralCodeJpaEntity::new);
    if (e.id != null && e.version != a.version())
      throw new org.springframework.orm.ObjectOptimisticLockingFailureException(
          ReferralCode.class, a.id());
    ReferralCodePersistenceMapper.copy(a, e);
    return ReferralCodePersistenceMapper.toDomain(jpa.saveAndFlush(e));
  }

  public void delete(UUID id) {
    jpa.deleteById(id);
  }

  public java.util.List<ReferralCode> findByWorkerId(UUID value) {
    return jpa.findByWorkerId(value).stream().map(ReferralCodePersistenceMapper::toDomain).toList();
  }

  public java.util.List<ReferralCode> findByCode(String value) {
    return jpa.findByCode(value).stream().map(ReferralCodePersistenceMapper::toDomain).toList();
  }
}
