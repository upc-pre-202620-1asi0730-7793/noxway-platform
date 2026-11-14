package com.noctiva.noxway.subscriptions.domain.model.aggregates;

import static com.noctiva.noxway.shared.domain.Rules.require;

import com.noctiva.noxway.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import com.noctiva.noxway.subscriptions.domain.model.events.SubscriptionActivatedEvent;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class Subscription extends AbstractDomainAggregateRoot<Subscription> {

  private final UUID id;
  private final UUID workerId;
  private final UUID planId;
  private String status;
  private Instant startsAt;
  private Instant endsAt;
  private final Instant createdAt;
  private final long version;

  private Subscription(
      UUID id,
      UUID workerId,
      UUID planId,
      String status,
      Instant startsAt,
      Instant endsAt,
      Instant createdAt,
      long version) {
    this.id = id;
    this.workerId = workerId;
    this.planId = planId;
    this.status = status;
    this.startsAt = startsAt;
    this.endsAt = endsAt;
    this.createdAt = createdAt;
    this.version = version;
  }

  public static Subscription create(
      UUID workerId, UUID planId, String status, Instant startsAt, Instant endsAt) {
    Objects.requireNonNull(workerId);
    Objects.requireNonNull(planId);
    require(endsAt != null && startsAt != null && !endsAt.isBefore(startsAt), "Invalid period");
    var sub =
        new Subscription(
            UUID.randomUUID(), workerId, planId, status, startsAt, endsAt, Instant.now(), 0L);
    if ("ACTIVE".equals(status)) {
      sub.registerEvent(
          new SubscriptionActivatedEvent(sub.id, workerId, planId, Instant.now()));
    }
    return sub;
  }

  public static Subscription reconstitute(
      UUID id,
      UUID workerId,
      UUID planId,
      String status,
      Instant startsAt,
      Instant endsAt,
      Instant createdAt,
      long version) {
    return new Subscription(id, workerId, planId, status, startsAt, endsAt, createdAt, version);
  }

  public void activateFrom(Instant now) {
    this.status = "ACTIVE";
    this.startsAt = now;
    this.endsAt = now.plus(Duration.ofDays(30));
    registerEvent(new SubscriptionActivatedEvent(id, workerId, planId, now));
  }

  public void extendDays(int days) {
    require(days > 0, "Extension days must be positive");
    this.endsAt = this.endsAt.plus(Duration.ofDays(days));
  }

  public void cancel() {
    require(!"CANCELLED".equals(status), "Subscription already cancelled");
    this.status = "CANCELLED";
  }

  public boolean isActiveAt(Instant now) {
    return "ACTIVE".equals(status) && endsAt.isAfter(now);
  }

  public UUID getId() {
    return id;
  }

  public UUID getWorkerId() {
    return workerId;
  }

  public UUID getPlanId() {
    return planId;
  }

  public String getStatus() {
    return status;
  }

  public Instant getStartsAt() {
    return startsAt;
  }

  public Instant getEndsAt() {
    return endsAt;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public long getVersion() {
    return version;
  }
}
