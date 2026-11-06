package com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.mappers;

import com.noctiva.noxway.subscriptions.domain.model.aggregates.ReferralReward;
import com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.entities.ReferralRewardJpaEntity;

public final class ReferralRewardPersistenceMapper {
  private ReferralRewardPersistenceMapper() {}

  public static ReferralReward toDomain(ReferralRewardJpaEntity e) {
    return new ReferralReward(
        e.id, e.workerId, e.referralUseId, e.days, e.status, e.createdAt, e.version);
  }

  public static void copy(ReferralReward a, ReferralRewardJpaEntity e) {
    e.id = a.id();
    e.workerId = a.workerId();
    e.referralUseId = a.referralUseId();
    e.days = a.days();
    e.status = a.status();
    e.createdAt = a.createdAt();
  }
}
