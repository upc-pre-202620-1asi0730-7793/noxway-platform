package com.noctiva.noxway.commuting.infrastructure.persistence.jpa.mappers;

import com.noctiva.noxway.commuting.domain.model.aggregates.TelemetryPing;
import com.noctiva.noxway.commuting.infrastructure.persistence.jpa.entities.TelemetryPingJpaEntity;

public final class TelemetryPingPersistenceMapper {
  private TelemetryPingPersistenceMapper() {}

  public static TelemetryPing toDomain(TelemetryPingJpaEntity e) {
    return new TelemetryPing(
        e.id, e.commuteId, e.latitude, e.longitude, e.speed, e.recordedAt, e.createdAt, e.version);
  }

  public static void copy(TelemetryPing a, TelemetryPingJpaEntity e) {
    e.id = a.id();
    e.commuteId = a.commuteId();
    e.latitude = a.latitude();
    e.longitude = a.longitude();
    e.speed = a.speed();
    e.recordedAt = a.recordedAt();
    e.createdAt = a.createdAt();
  }
}
