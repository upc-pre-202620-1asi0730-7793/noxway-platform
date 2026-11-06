package com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.mappers;

import com.noctiva.noxway.subscriptions.domain.model.aggregates.BenefitClaim;
import com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.entities.BenefitClaimJpaEntity;

public final class BenefitClaimPersistenceMapper {
  private BenefitClaimPersistenceMapper() {}

  public static BenefitClaim toDomain(BenefitClaimJpaEntity e) {
    return new BenefitClaim(e.id, e.workerId, e.benefitId, e.code, e.createdAt, e.version);
  }

  public static void copy(BenefitClaim a, BenefitClaimJpaEntity e) {
    e.id = a.id();
    e.workerId = a.workerId();
    e.benefitId = a.benefitId();
    e.code = a.code();
    e.createdAt = a.createdAt();
  }
}
