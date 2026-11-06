package com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.mappers;

import com.noctiva.noxway.subscriptions.domain.model.aggregates.Subscription;
import com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.entities.SubscriptionJpaEntity;

public final class SubscriptionPersistenceMapper {
  private SubscriptionPersistenceMapper() {}

  public static Subscription toDomain(SubscriptionJpaEntity e) {
    return new Subscription(
        e.id, e.workerId, e.planId, e.status, e.startsAt, e.endsAt, e.createdAt, e.version);
  }

  public static void copy(Subscription a, SubscriptionJpaEntity e) {
    e.id = a.id();
    e.workerId = a.workerId();
    e.planId = a.planId();
    e.status = a.status();
    e.startsAt = a.startsAt();
    e.endsAt = a.endsAt();
    e.createdAt = a.createdAt();
  }
}
