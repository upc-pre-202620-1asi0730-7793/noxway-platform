package com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.mappers;

import com.noctiva.noxway.subscriptions.domain.model.aggregates.CollectiveBenefit;
import com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.entities.CollectiveBenefitJpaEntity;

public final class CollectiveBenefitPersistenceMapper {
  private CollectiveBenefitPersistenceMapper() {}

  public static CollectiveBenefit toDomain(CollectiveBenefitJpaEntity e) {
    return new CollectiveBenefit(
        e.id, e.name, e.description, e.planCode, e.active, e.createdAt, e.version);
  }

  public static void copy(CollectiveBenefit a, CollectiveBenefitJpaEntity e) {
    e.id = a.id();
    e.name = a.name();
    e.description = a.description();
    e.planCode = a.planCode();
    e.active = a.active();
    e.createdAt = a.createdAt();
  }
}
