package com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.mappers;

import com.noctiva.noxway.subscriptions.domain.model.aggregates.ReferralCode;
import com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.entities.ReferralCodeJpaEntity;

public final class ReferralCodePersistenceMapper {
  private ReferralCodePersistenceMapper() {}

  public static ReferralCode toDomain(ReferralCodeJpaEntity e) {
    return new ReferralCode(e.id, e.workerId, e.code, e.createdAt, e.version);
  }

  public static void copy(ReferralCode a, ReferralCodeJpaEntity e) {
    e.id = a.id();
    e.workerId = a.workerId();
    e.code = a.code();
    e.createdAt = a.createdAt();
  }
}
