package com.noctiva.noxway.subscriptions.domain.model.aggregates;

import java.time.Instant;
import java.util.UUID;

public record CollectiveBenefit(
    UUID id,
    String name,
    String description,
    String planCode,
    boolean active,
    Instant createdAt,
    long version) {
  public static CollectiveBenefit create(
      String name, String description, String planCode, boolean active) {
    return new CollectiveBenefit(
        UUID.randomUUID(), name, description, planCode, active, Instant.now(), 0);
  }

  public CollectiveBenefit withName(String value) {
    return new CollectiveBenefit(id, value, description, planCode, active, createdAt, version);
  }

  public CollectiveBenefit withDescription(String value) {
    return new CollectiveBenefit(id, name, value, planCode, active, createdAt, version);
  }

  public CollectiveBenefit withPlanCode(String value) {
    return new CollectiveBenefit(id, name, description, value, active, createdAt, version);
  }

  public CollectiveBenefit withActive(boolean value) {
    return new CollectiveBenefit(id, name, description, planCode, value, createdAt, version);
  }
}
