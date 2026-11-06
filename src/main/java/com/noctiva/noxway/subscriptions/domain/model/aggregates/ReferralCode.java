package com.noctiva.noxway.subscriptions.domain.model.aggregates;

import java.time.Instant;
import java.util.UUID;

public record ReferralCode(UUID id, UUID workerId, String code, Instant createdAt, long version) {
  public static ReferralCode create(UUID workerId, String code) {
    return new ReferralCode(UUID.randomUUID(), workerId, code, Instant.now(), 0);
  }

  public ReferralCode withWorkerId(UUID value) {
    return new ReferralCode(id, value, code, createdAt, version);
  }

  public ReferralCode withCode(String value) {
    return new ReferralCode(id, workerId, value, createdAt, version);
  }
}
