package com.noctiva.noxway.subscriptions.domain.model.aggregates;

import com.noctiva.noxway.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import java.time.Instant;
import java.util.UUID;

public class BenefitClaim extends AbstractDomainAggregateRoot<BenefitClaim> {

  private final UUID id;
  private final UUID workerId;
  private final UUID benefitId;
  private final String code;
  private final Instant createdAt;
  private final long version;

  private BenefitClaim(
      UUID id, UUID workerId, UUID benefitId, String code, Instant createdAt, long version) {
    this.id = id;
    this.workerId = workerId;
    this.benefitId = benefitId;
    this.code = code;
    this.createdAt = createdAt;
    this.version = version;
  }

  public static BenefitClaim create(UUID workerId, UUID benefitId, String code) {
    return new BenefitClaim(
        UUID.randomUUID(), workerId, benefitId, code, Instant.now(), 0L);
  }

  public static BenefitClaim reconstitute(
      UUID id, UUID workerId, UUID benefitId, String code, Instant createdAt, long version) {
    return new BenefitClaim(id, workerId, benefitId, code, createdAt, version);
  }

  public UUID getId() {
    return id;
  }

  public UUID getWorkerId() {
    return workerId;
  }

  public UUID getBenefitId() {
    return benefitId;
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
