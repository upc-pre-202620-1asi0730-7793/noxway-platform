package com.noctiva.noxway.subscriptions.domain.model.aggregates;

import java.time.Instant;
import java.util.UUID;

public record ReferralUse(
    UUID id, UUID referralId, UUID referredWorkerId, Instant createdAt, long version) {
  public static ReferralUse create(UUID referralId, UUID referredWorkerId) {
    return new ReferralUse(UUID.randomUUID(), referralId, referredWorkerId, Instant.now(), 0);
  }

  public ReferralUse withReferralId(UUID value) {
    return new ReferralUse(id, value, referredWorkerId, createdAt, version);
  }

  public ReferralUse withReferredWorkerId(UUID value) {
    return new ReferralUse(id, referralId, value, createdAt, version);
  }
}
