package com.noctiva.noxway.wellness.infrastructure.persistence.jpa.assemblers;

import com.noctiva.noxway.wellness.domain.model.aggregates.RestSession;
import com.noctiva.noxway.wellness.infrastructure.persistence.jpa.entities.RestSessionPersistenceEntity;

public final class RestSessionPersistenceAssembler {
  private RestSessionPersistenceAssembler() {}

  public static RestSession toDomain(RestSessionPersistenceEntity e) {
    return RestSession.reconstitute(
        e.getId(),
        e.getWorkerId(),
        e.getStartTime(),
        e.getEndTime(),
        e.getQuality(),
        e.getNotes(),
        e.getCreatedAt(),
        e.getVersion());
  }

  public static RestSessionPersistenceEntity toPersistence(RestSession s) {
    var e = new RestSessionPersistenceEntity();
    e.setId(s.getId());
    e.setWorkerId(s.getWorkerId());
    e.setStartTime(s.getStartTime());
    e.setEndTime(s.getEndTime());
    e.setQuality(s.getQuality());
    e.setNotes(s.getNotes());
    e.setCreatedAt(s.getCreatedAt());
    e.setVersion(s.getVersion());
    return e;
  }
}
