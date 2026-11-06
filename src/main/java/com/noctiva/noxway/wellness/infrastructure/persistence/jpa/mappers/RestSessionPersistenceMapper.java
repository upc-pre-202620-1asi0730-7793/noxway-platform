package com.noctiva.noxway.wellness.infrastructure.persistence.jpa.mappers;

import com.noctiva.noxway.wellness.domain.model.aggregates.RestSession;
import com.noctiva.noxway.wellness.infrastructure.persistence.jpa.entities.RestSessionJpaEntity;

public final class RestSessionPersistenceMapper {
  private RestSessionPersistenceMapper() {}

  public static RestSession toDomain(RestSessionJpaEntity e) {
    return new RestSession(
        e.id, e.workerId, e.startTime, e.endTime, e.quality, e.notes, e.createdAt, e.version);
  }

  public static void copy(RestSession a, RestSessionJpaEntity e) {
    e.id = a.id();
    e.workerId = a.workerId();
    e.startTime = a.startTime();
    e.endTime = a.endTime();
    e.quality = a.quality();
    e.notes = a.notes();
    e.createdAt = a.createdAt();
  }
}
