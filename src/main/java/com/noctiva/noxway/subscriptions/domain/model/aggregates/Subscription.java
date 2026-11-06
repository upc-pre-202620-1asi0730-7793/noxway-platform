package com.noctiva.noxway.subscriptions.domain.model.aggregates;

import java.time.Instant;
import java.util.UUID;

public record Subscription(
    UUID id,
    UUID workerId,
    UUID planId,
    String status,
    Instant startsAt,
    Instant endsAt,
    Instant createdAt,
    long version) {
  public static Subscription create(
      UUID workerId, UUID planId, String status, Instant startsAt, Instant endsAt) {
    return new Subscription(
        UUID.randomUUID(), workerId, planId, status, startsAt, endsAt, Instant.now(), 0);
  }

  public Subscription withWorkerId(UUID value) {
    return new Subscription(id, value, planId, status, startsAt, endsAt, createdAt, version);
  }

  public Subscription withPlanId(UUID value) {
    return new Subscription(id, workerId, value, status, startsAt, endsAt, createdAt, version);
  }

  public Subscription withStatus(String value) {
    return new Subscription(id, workerId, planId, value, startsAt, endsAt, createdAt, version);
  }

  public Subscription withStartsAt(Instant value) {
    return new Subscription(id, workerId, planId, status, value, endsAt, createdAt, version);
  }

  public Subscription withEndsAt(Instant value) {
    return new Subscription(id, workerId, planId, status, startsAt, value, createdAt, version);
  }
}
