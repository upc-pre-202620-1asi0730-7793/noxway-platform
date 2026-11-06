package com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.mappers;

import com.noctiva.noxway.subscriptions.domain.model.aggregates.Plan;
import com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.entities.PlanJpaEntity;

public final class PlanPersistenceMapper {
  private PlanPersistenceMapper() {}

  public static Plan toDomain(PlanJpaEntity e) {
    return new Plan(
        e.id,
        e.code,
        e.name,
        e.monthlyPrice,
        e.maxContacts,
        e.maxWorkers,
        e.active,
        e.createdAt,
        e.version);
  }

  public static void copy(Plan a, PlanJpaEntity e) {
    e.id = a.id();
    e.code = a.code();
    e.name = a.name();
    e.monthlyPrice = a.monthlyPrice();
    e.maxContacts = a.maxContacts();
    e.maxWorkers = a.maxWorkers();
    e.active = a.active();
    e.createdAt = a.createdAt();
  }
}
