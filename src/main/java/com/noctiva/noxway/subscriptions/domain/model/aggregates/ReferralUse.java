package com.noctiva.noxway.subscriptions.domain.model.aggregates;

import com.noctiva.noxway.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import java.time.Instant;
import java.util.UUID;

public class ReferralUse extends AbstractDomainAggregateRoot<ReferralUse> {

  private final UUID id;
  private final UUID referralId;
  private final UUID referredWorkerId;
  private final Instant createdAt;
  private final long version;

  private ReferralUse(
      UUID id, UUID referralId, UUID referredWorkerId, Instant createdAt, long version) {
    this.id = id;
    this.referralId = referralId;
    this.referredWorkerId = referredWorkerId;
    this.createdAt = createdAt;
    this.version = version;
  }

  public static ReferralUse create(UUID referralId, UUID referredWorkerId) {
    return new ReferralUse(
        UUID.randomUUID(), referralId, referredWorkerId, Instant.now(), 0L);
  }

  public static ReferralUse reconstitute(
      UUID id, UUID referralId, UUID referredWorkerId, Instant createdAt, long version) {
    return new ReferralUse(id, referralId, referredWorkerId, createdAt, version);
  }

  public UUID getId() {
    return id;
  }

  public UUID getReferralId() {
    return referralId;
  }

  public UUID getReferredWorkerId() {
    return referredWorkerId;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public long getVersion() {
    return version;
  }
}
