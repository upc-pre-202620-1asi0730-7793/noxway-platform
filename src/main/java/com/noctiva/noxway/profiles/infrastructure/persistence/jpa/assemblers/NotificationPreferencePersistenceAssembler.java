package com.noctiva.noxway.profiles.infrastructure.persistence.jpa.assemblers;

import com.noctiva.noxway.profiles.domain.model.aggregates.NotificationPreference;
import com.noctiva.noxway.profiles.infrastructure.persistence.jpa.entities.NotificationPreferencePersistenceEntity;

public final class NotificationPreferencePersistenceAssembler {
  private NotificationPreferencePersistenceAssembler() {}

  public static NotificationPreference toDomain(NotificationPreferencePersistenceEntity e) {
    return NotificationPreference.reconstitute(
        e.getId(),
        e.getAccountId(),
        e.getChannel(),
        e.isEnabled(),
        e.getPriorityStart(),
        e.getPriorityEnd(),
        e.getTimeZone(),
        e.getCreatedAt(),
        e.getVersion());
  }

  public static NotificationPreferencePersistenceEntity toPersistence(NotificationPreference p) {
    var e = new NotificationPreferencePersistenceEntity();
    e.setId(p.getId());
    e.setAccountId(p.getAccountId());
    e.setChannel(p.getChannel());
    e.setEnabled(p.isEnabled());
    e.setPriorityStart(p.getPriorityStart());
    e.setPriorityEnd(p.getPriorityEnd());
    e.setTimeZone(p.getTimeZone());
    e.setCreatedAt(p.getCreatedAt());
    e.setVersion(p.getVersion());
    return e;
  }
}
