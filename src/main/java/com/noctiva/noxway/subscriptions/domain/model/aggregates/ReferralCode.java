package com.noctiva.noxway.subscriptions.domain.model.aggregates;

import com.noctiva.noxway.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import java.time.Instant;
import java.util.UUID;

public class ReferralCode extends AbstractDomainAggregateRoot<ReferralCode> {

  private final UUID id;
  private final UUID workerId;
  private final String code;
  private final Instant createdAt;
  private final long version;

  private ReferralCode(UUID id, UUID workerId, String code, Instant createdAt, long version) {
    this.id = id;
    this.workerId = workerId;
    this.code = code;
    this.createdAt = createdAt;
    this.version = version;
  }

  public static ReferralCode create(UUID workerId, String code) {
    return new ReferralCode(UUID.randomUUID(), workerId, code, Instant.now(), 0L);
  }

  public static ReferralCode reconstitute(
      UUID id, UUID workerId, String code, Instant createdAt, long version) {
    return new ReferralCode(id, workerId, code, createdAt, version);
  }

  public UUID getId() {
    return id;
  }

  public UUID getWorkerId() {
    return workerId;
  }

  public String getCode() {
    return code;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public long getVersion() {
    return version;
  }
}
