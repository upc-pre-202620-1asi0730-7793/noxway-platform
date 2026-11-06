package com.noctiva.noxway.commuting.infrastructure.persistence.jpa.mappers;

import com.noctiva.noxway.commuting.domain.model.aggregates.Alert;
import com.noctiva.noxway.commuting.infrastructure.persistence.jpa.entities.AlertJpaEntity;

public final class AlertPersistenceMapper {
  private AlertPersistenceMapper() {}

  public static Alert toDomain(AlertJpaEntity e) {
    return new Alert(
        e.id,
        e.commuteId,
        e.incidentId,
        e.contactId,
        e.channel,
        e.status,
        e.message,
        e.deliveredAt,
        e.createdAt,
        e.version);
  }

  public static void copy(Alert a, AlertJpaEntity e) {
    e.id = a.id();
    e.commuteId = a.commuteId();
    e.incidentId = a.incidentId();
    e.contactId = a.contactId();
    e.channel = a.channel();
    e.status = a.status();
    e.message = a.message();
    e.deliveredAt = a.deliveredAt();
    e.createdAt = a.createdAt();
  }
}
