package com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.adapters;

import com.noctiva.noxway.subscriptions.domain.model.aggregates.BenefitClaim;
import com.noctiva.noxway.subscriptions.domain.repositories.BenefitClaimRepository;
import com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.entities.BenefitClaimJpaEntity;
import com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.mappers.BenefitClaimPersistenceMapper;
import com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.repositories.BenefitClaimJpaRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class BenefitClaimRepositoryAdapter implements BenefitClaimRepository {
  private final BenefitClaimJpaRepository jpa;

  public BenefitClaimRepositoryAdapter(BenefitClaimJpaRepository jpa) {
    this.jpa = jpa;
  }

  public Optional<BenefitClaim> findById(UUID id) {
    return jpa.findById(id).map(BenefitClaimPersistenceMapper::toDomain);
  }

  public java.util.List<BenefitClaim> findAll() {
    return jpa.findAll().stream().map(BenefitClaimPersistenceMapper::toDomain).toList();
  }

  public BenefitClaim save(BenefitClaim a) {
    var e = jpa.findById(a.id()).orElseGet(BenefitClaimJpaEntity::new);
    if (e.id != null && e.version != a.version())
      throw new org.springframework.orm.ObjectOptimisticLockingFailureException(
          BenefitClaim.class, a.id());
    BenefitClaimPersistenceMapper.copy(a, e);
    return BenefitClaimPersistenceMapper.toDomain(jpa.saveAndFlush(e));
  }

  public void delete(UUID id) {
    jpa.deleteById(id);
  }

  public java.util.List<BenefitClaim> findByWorkerId(UUID value) {
    return jpa.findByWorkerId(value).stream().map(BenefitClaimPersistenceMapper::toDomain).toList();
  }

  public java.util.List<BenefitClaim> findByBenefitId(UUID value) {
    return jpa.findByBenefitId(value).stream()
        .map(BenefitClaimPersistenceMapper::toDomain)
        .toList();
  }

  public java.util.List<BenefitClaim> findByCode(String value) {
    return jpa.findByCode(value).stream().map(BenefitClaimPersistenceMapper::toDomain).toList();
  }
}
