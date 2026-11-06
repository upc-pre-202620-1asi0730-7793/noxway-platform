package com.noctiva.noxway.subscriptions.domain.model.aggregates;

import java.time.Instant;
import java.util.UUID;

public record ReferralReward(
    UUID id,
    UUID workerId,
    UUID referralUseId,
    int days,
    String status,
    Instant createdAt,
    long version) {
  public static ReferralReward create(UUID workerId, UUID referralUseId, int days, String status) {
    return new ReferralReward(
        UUID.randomUUID(), workerId, referralUseId, days, status, Instant.now(), 0);
  }

  public ReferralReward withWorkerId(UUID value) {
    return new ReferralReward(id, value, referralUseId, days, status, createdAt, version);
  }

  public ReferralReward withReferralUseId(UUID value) {
    return new ReferralReward(id, workerId, value, days, status, createdAt, version);
  }

  public ReferralReward withDays(int value) {
    return new ReferralReward(id, workerId, referralUseId, value, status, createdAt, version);
  }

  public ReferralReward withStatus(String value) {
    return new ReferralReward(id, workerId, referralUseId, days, value, createdAt, version);
  }
}
