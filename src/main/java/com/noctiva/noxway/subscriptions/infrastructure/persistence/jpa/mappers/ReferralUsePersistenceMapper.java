package com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.mappers;

import com.noctiva.noxway.subscriptions.domain.model.aggregates.ReferralUse;
import com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.entities.ReferralUseJpaEntity;

public final class ReferralUsePersistenceMapper {
  private ReferralUsePersistenceMapper() {}

  public static ReferralUse toDomain(ReferralUseJpaEntity e) {
    return new ReferralUse(e.id, e.referralId, e.referredWorkerId, e.createdAt, e.version);
  }

  public static void copy(ReferralUse a, ReferralUseJpaEntity e) {
    e.id = a.id();
    e.referralId = a.referralId();
    e.referredWorkerId = a.referredWorkerId();
    e.createdAt = a.createdAt();
  }
}
