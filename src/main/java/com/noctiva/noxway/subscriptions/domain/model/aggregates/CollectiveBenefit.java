package com.noctiva.noxway.subscriptions.domain.model.aggregates;

import static com.noctiva.noxway.shared.domain.Rules.require;

import com.noctiva.noxway.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import java.time.Instant;
import java.util.UUID;

public class CollectiveBenefit extends AbstractDomainAggregateRoot<CollectiveBenefit> {

  private final UUID id;
  private final String name;
  private final String description;
  private final String planCode;
  private final boolean active;
  private final Instant createdAt;
  private final long version;

  private CollectiveBenefit(
      UUID id,
      String name,
      String description,
      String planCode,
      boolean active,
      Instant createdAt,
      long version) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.planCode = planCode;
    this.active = active;
    this.createdAt = createdAt;
    this.version = version;
  }

  public static CollectiveBenefit create(
      String name, String description, String planCode, boolean active) {
    require(name != null && !name.isBlank(), "Benefit name is required");
    require(description != null && !description.isBlank(), "Benefit description is required");
    require(planCode != null && !planCode.isBlank(), "Plan code is required");
    return new CollectiveBenefit(
        UUID.randomUUID(), name.trim(), description.trim(), planCode.trim(), active, Instant.now(), 0L);
  }

  public static CollectiveBenefit reconstitute(
      UUID id,
      String name,
      String description,
      String planCode,
      boolean active,
      Instant createdAt,
      long version) {
    return new CollectiveBenefit(id, name, description, planCode, active, createdAt, version);
  }

  public UUID getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public String getPlanCode() {
    return planCode;
  }

  public boolean isActive() {
    return active;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public long getVersion() {
    return version;
  }
}
