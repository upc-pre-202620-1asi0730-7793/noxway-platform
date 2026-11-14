package com.noctiva.noxway.subscriptions.domain.model.aggregates;

import static com.noctiva.noxway.shared.domain.Rules.require;

import com.noctiva.noxway.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import java.time.Instant;
import java.util.UUID;

public class ReferralReward extends AbstractDomainAggregateRoot<ReferralReward> {

  private final UUID id;
  private final UUID workerId;
  private final UUID referralUseId;
  private final int days;
  private String status;
  private final Instant createdAt;
  private final long version;

  private ReferralReward(
      UUID id,
      UUID workerId,
      UUID referralUseId,
      int days,
      String status,
      Instant createdAt,
      long version) {
    this.id = id;
    this.workerId = workerId;
    this.referralUseId = referralUseId;
    this.days = days;
    this.status = status;
    this.createdAt = createdAt;
    this.version = version;
  }

  public static ReferralReward createAvailable(UUID workerId, UUID referralUseId, int days) {
    require(days > 0, "Reward days must be positive");
    return new ReferralReward(
        UUID.randomUUID(), workerId, referralUseId, days, "AVAILABLE", Instant.now(), 0L);
  }

  public static ReferralReward reconstitute(
      UUID id,
      UUID workerId,
      UUID referralUseId,
      int days,
      String status,
      Instant createdAt,
      long version) {
    return new ReferralReward(id, workerId, referralUseId, days, status, createdAt, version);
  }

  public void redeem() {
    require("AVAILABLE".equals(status), "Reward already redeemed");
    this.status = "REDEEMED";
  }

  public UUID getId() {
    return id;
  }

  public UUID getWorkerId() {
    return workerId;
  }

  public UUID getReferralUseId() {
    return referralUseId;
  }

  public int getDays() {
    return days;
  }

  public String getStatus() {
    return status;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public long getVersion() {
    return version;
  }
}
