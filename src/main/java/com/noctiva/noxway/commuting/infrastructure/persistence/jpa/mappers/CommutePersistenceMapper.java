package com.noctiva.noxway.commuting.infrastructure.persistence.jpa.mappers;

import com.noctiva.noxway.commuting.domain.model.aggregates.Commute;
import com.noctiva.noxway.commuting.infrastructure.persistence.jpa.entities.CommuteJpaEntity;

public final class CommutePersistenceMapper {
  private CommutePersistenceMapper() {}

  public static Commute toDomain(CommuteJpaEntity e) {
    return new Commute(
        e.id,
        e.workerId,
        e.origin,
        e.destination,
        e.transport,
        e.startTime,
        e.estimatedArrivalTime,
        e.toleranceMinutes,
        e.status,
        e.arrivalTime,
        e.delayUntil,
        e.createdAt,
        e.version);
  }

  public static void copy(Commute a, CommuteJpaEntity e) {
    e.id = a.id();
    e.workerId = a.workerId();
    e.origin = a.origin();
    e.destination = a.destination();
    e.transport = a.transport();
    e.startTime = a.startTime();
    e.estimatedArrivalTime = a.estimatedArrivalTime();
    e.toleranceMinutes = a.toleranceMinutes();
    e.status = a.status();
    e.arrivalTime = a.arrivalTime();
    e.delayUntil = a.delayUntil();
    e.createdAt = a.createdAt();
  }
}
