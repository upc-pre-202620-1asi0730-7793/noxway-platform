package com.noctiva.noxway.subscriptions.domain.model.aggregates;

import java.time.Instant;
import java.util.UUID;

public record BenefitClaim(
    UUID id, UUID workerId, UUID benefitId, String code, Instant createdAt, long version) {
  public static BenefitClaim create(UUID workerId, UUID benefitId, String code) {
    return new BenefitClaim(UUID.randomUUID(), workerId, benefitId, code, Instant.now(), 0);
  }

  public BenefitClaim withWorkerId(UUID value) {
    return new BenefitClaim(id, value, benefitId, code, createdAt, version);
  }

  public BenefitClaim withBenefitId(UUID value) {
    return new BenefitClaim(id, workerId, value, code, createdAt, version);
  }

  public BenefitClaim withCode(String value) {
    return new BenefitClaim(id, workerId, benefitId, value, createdAt, version);
  }
}
