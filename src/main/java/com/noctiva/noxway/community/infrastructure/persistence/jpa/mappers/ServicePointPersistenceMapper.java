package com.noctiva.noxway.community.infrastructure.persistence.jpa.mappers;

import com.noctiva.noxway.community.domain.model.aggregates.ServicePoint;
import com.noctiva.noxway.community.infrastructure.persistence.jpa.entities.ServicePointJpaEntity;

public final class ServicePointPersistenceMapper {
  private ServicePointPersistenceMapper() {}

  public static ServicePoint toDomain(ServicePointJpaEntity e) {
    return new ServicePoint(
        e.id,
        e.reportedBy,
        e.name,
        e.type,
        e.latitude,
        e.longitude,
        e.timeZone,
        e.status,
        e.createdAt,
        e.version);
  }

  public static void copy(ServicePoint a, ServicePointJpaEntity e) {
    e.id = a.id();
    e.reportedBy = a.reportedBy();
    e.name = a.name();
    e.type = a.type();
    e.latitude = a.latitude();
    e.longitude = a.longitude();
    e.timeZone = a.timeZone();
    e.status = a.status();
    e.createdAt = a.createdAt();
  }
}
