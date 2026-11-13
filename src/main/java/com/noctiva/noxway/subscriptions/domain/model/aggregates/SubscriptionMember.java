package com.noctiva.noxway.subscriptions.domain.model.aggregates;

import static com.noctiva.noxway.shared.domain.Rules.require;

import com.noctiva.noxway.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import java.time.Instant;
import java.util.UUID;

public class SubscriptionMember extends AbstractDomainAggregateRoot<SubscriptionMember> {

  private final UUID id;
  private final UUID subscriptionId;
  private final UUID workerId;
  private String status;
  private final Instant createdAt;
  private final long version;

  private SubscriptionMember(
      UUID id, UUID subscriptionId, UUID workerId, String status, Instant createdAt, long version) {
    this.id = id;
    this.subscriptionId = subscriptionId;
    this.workerId = workerId;
    this.status = status;
    this.createdAt = createdAt;
    this.version = version;
  }

  public static SubscriptionMember invite(UUID subscriptionId, UUID workerId) {
    return new SubscriptionMember(
        UUID.randomUUID(), subscriptionId, workerId, "PENDING", Instant.now(), 0L);
  }

  public static SubscriptionMember reconstitute(
      UUID id, UUID subscriptionId, UUID workerId, String status, Instant createdAt, long version) {
    return new SubscriptionMember(id, subscriptionId, workerId, status, createdAt, version);
  }

  public void accept(UUID actorId) {
    require(workerId.equals(actorId), "Only invited member can accept");
    require("PENDING".equals(status), "Invitation is not pending");
    this.status = "ACCEPTED";
  }

  public void reject(UUID actorId) {
    require(workerId.equals(actorId), "Only invited member can reject");
    require("PENDING".equals(status), "Invitation is not pending");
    this.status = "REJECTED";
  }

  public void revoke() {
    require(!"REVOKED".equals(status), "Membership already revoked");
    this.status = "REVOKED";
  }

  public UUID getId() {
    return id;
  }

  public UUID getSubscriptionId() {
    return subscriptionId;
  }

  public UUID getWorkerId() {
    return workerId;
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
