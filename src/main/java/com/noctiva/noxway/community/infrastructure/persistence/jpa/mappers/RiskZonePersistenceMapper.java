package com.noctiva.noxway.community.infrastructure.persistence.jpa.mappers;

import com.noctiva.noxway.community.domain.model.aggregates.RiskZone;
import com.noctiva.noxway.community.infrastructure.persistence.jpa.entities.RiskZoneJpaEntity;

public final class RiskZonePersistenceMapper {
  private RiskZonePersistenceMapper() {}

  public static RiskZone toDomain(RiskZoneJpaEntity e) {
    return new RiskZone(
        e.id,
        e.reportedBy,
        e.description,
        e.riskLevel,
        e.latitude,
        e.longitude,
        e.status,
        e.createdAt,
        e.version);
  }

  public static void copy(RiskZone a, RiskZoneJpaEntity e) {
    e.id = a.id();
    e.reportedBy = a.reportedBy();
    e.description = a.description();
    e.riskLevel = a.riskLevel();
    e.latitude = a.latitude();
    e.longitude = a.longitude();
    e.status = a.status();
    e.createdAt = a.createdAt();
  }
}
