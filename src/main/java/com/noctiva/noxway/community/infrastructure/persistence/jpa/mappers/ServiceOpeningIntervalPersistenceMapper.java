package com.noctiva.noxway.community.infrastructure.persistence.jpa.mappers;

import com.noctiva.noxway.community.domain.model.aggregates.ServiceOpeningInterval;
import com.noctiva.noxway.community.infrastructure.persistence.jpa.entities.ServiceOpeningIntervalJpaEntity;

public final class ServiceOpeningIntervalPersistenceMapper {
  private ServiceOpeningIntervalPersistenceMapper() {}

  public static ServiceOpeningInterval toDomain(ServiceOpeningIntervalJpaEntity e) {
    return new ServiceOpeningInterval(
        e.id,
        e.serviceId,
        e.dayOfWeek,
        e.opensAt,
        e.closesAt,
        e.closesNextDay,
        e.is24Hours,
        e.createdAt,
        e.version);
  }

  public static void copy(ServiceOpeningInterval a, ServiceOpeningIntervalJpaEntity e) {
    e.id = a.id();
    e.serviceId = a.serviceId();
    e.dayOfWeek = a.dayOfWeek();
    e.opensAt = a.opensAt();
    e.closesAt = a.closesAt();
    e.closesNextDay = a.closesNextDay();
    e.is24Hours = a.is24Hours();
    e.createdAt = a.createdAt();
  }
}
