package com.noctiva.noxway.subscriptions.domain.model.aggregates;

import java.time.Instant;
import java.util.UUID;

public record SubscriptionMember(
    UUID id, UUID subscriptionId, UUID workerId, String status, Instant createdAt, long version) {
  public static SubscriptionMember create(UUID subscriptionId, UUID workerId, String status) {
    return new SubscriptionMember(
        UUID.randomUUID(), subscriptionId, workerId, status, Instant.now(), 0);
  }

  public SubscriptionMember withSubscriptionId(UUID value) {
    return new SubscriptionMember(id, value, workerId, status, createdAt, version);
  }

  public SubscriptionMember withWorkerId(UUID value) {
    return new SubscriptionMember(id, subscriptionId, value, status, createdAt, version);
  }

  public SubscriptionMember withStatus(String value) {
    return new SubscriptionMember(id, subscriptionId, workerId, value, createdAt, version);
  }
}
