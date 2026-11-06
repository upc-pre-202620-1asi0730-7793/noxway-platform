package com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.mappers;

import com.noctiva.noxway.subscriptions.domain.model.aggregates.SubscriptionMember;
import com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.entities.SubscriptionMemberJpaEntity;

public final class SubscriptionMemberPersistenceMapper {
  private SubscriptionMemberPersistenceMapper() {}

  public static SubscriptionMember toDomain(SubscriptionMemberJpaEntity e) {
    return new SubscriptionMember(
        e.id, e.subscriptionId, e.workerId, e.status, e.createdAt, e.version);
  }

  public static void copy(SubscriptionMember a, SubscriptionMemberJpaEntity e) {
    e.id = a.id();
    e.subscriptionId = a.subscriptionId();
    e.workerId = a.workerId();
    e.status = a.status();
    e.createdAt = a.createdAt();
  }
}
