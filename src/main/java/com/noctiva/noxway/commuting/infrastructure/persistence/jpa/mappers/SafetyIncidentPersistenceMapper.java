package com.noctiva.noxway.commuting.infrastructure.persistence.jpa.mappers;

import com.noctiva.noxway.commuting.domain.model.aggregates.SafetyIncident;
import com.noctiva.noxway.commuting.infrastructure.persistence.jpa.entities.SafetyIncidentJpaEntity;

public final class SafetyIncidentPersistenceMapper {
  private SafetyIncidentPersistenceMapper() {}

  public static SafetyIncident toDomain(SafetyIncidentJpaEntity e) {
    return new SafetyIncident(
        e.id,
        e.commuteId,
        e.workerId,
        e.type,
        e.description,
        e.status,
        e.reportedAt,
        e.createdAt,
        e.version);
  }

  public static void copy(SafetyIncident a, SafetyIncidentJpaEntity e) {
    e.id = a.id();
    e.commuteId = a.commuteId();
    e.workerId = a.workerId();
    e.type = a.type();
    e.description = a.description();
    e.status = a.status();
    e.reportedAt = a.reportedAt();
    e.createdAt = a.createdAt();
  }
}
