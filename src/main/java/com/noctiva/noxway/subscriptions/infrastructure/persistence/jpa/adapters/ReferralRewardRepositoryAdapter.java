package com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.adapters;

import com.noctiva.noxway.subscriptions.domain.model.aggregates.ReferralReward;
import com.noctiva.noxway.subscriptions.domain.repositories.ReferralRewardRepository;
import com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.entities.ReferralRewardJpaEntity;
import com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.mappers.ReferralRewardPersistenceMapper;
import com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.repositories.ReferralRewardJpaRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class ReferralRewardRepositoryAdapter implements ReferralRewardRepository {
  private final ReferralRewardJpaRepository jpa;

  public ReferralRewardRepositoryAdapter(ReferralRewardJpaRepository jpa) {
    this.jpa = jpa;
  }

  public Optional<ReferralReward> findById(UUID id) {
    return jpa.findById(id).map(ReferralRewardPersistenceMapper::toDomain);
  }

  public java.util.List<ReferralReward> findAll() {
    return jpa.findAll().stream().map(ReferralRewardPersistenceMapper::toDomain).toList();
  }

  public ReferralReward save(ReferralReward a) {
    var e = jpa.findById(a.id()).orElseGet(ReferralRewardJpaEntity::new);
    if (e.id != null && e.version != a.version())
      throw new org.springframework.orm.ObjectOptimisticLockingFailureException(
          ReferralReward.class, a.id());
    ReferralRewardPersistenceMapper.copy(a, e);
    return ReferralRewardPersistenceMapper.toDomain(jpa.saveAndFlush(e));
  }

  public void delete(UUID id) {
    jpa.deleteById(id);
  }

  public java.util.List<ReferralReward> findByWorkerId(UUID value) {
    return jpa.findByWorkerId(value).stream()
        .map(ReferralRewardPersistenceMapper::toDomain)
        .toList();
  }

  public java.util.List<ReferralReward> findByReferralUseId(UUID value) {
    return jpa.findByReferralUseId(value).stream()
        .map(ReferralRewardPersistenceMapper::toDomain)
        .toList();
  }

  public java.util.List<ReferralReward> findByStatus(String value) {
    return jpa.findByStatus(value).stream().map(ReferralRewardPersistenceMapper::toDomain).toList();
  }
}
