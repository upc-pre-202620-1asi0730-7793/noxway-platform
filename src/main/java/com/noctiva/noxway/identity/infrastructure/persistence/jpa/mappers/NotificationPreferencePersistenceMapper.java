package com.noctiva.noxway.identity.infrastructure.persistence.jpa.mappers;

import com.noctiva.noxway.identity.domain.model.aggregates.NotificationPreference;
import com.noctiva.noxway.identity.infrastructure.persistence.jpa.entities.NotificationPreferenceJpaEntity;

public final class NotificationPreferencePersistenceMapper {
  private NotificationPreferencePersistenceMapper() {}

  public static NotificationPreference toDomain(NotificationPreferenceJpaEntity e) {
    return new NotificationPreference(
        e.id,
        e.accountId,
        e.channel,
        e.enabled,
        e.priorityStart,
        e.priorityEnd,
        e.timeZone,
        e.createdAt,
        e.version);
  }

  public static void copy(NotificationPreference a, NotificationPreferenceJpaEntity e) {
    e.id = a.id();
    e.accountId = a.accountId();
    e.channel = a.channel();
    e.enabled = a.enabled();
    e.priorityStart = a.priorityStart();
    e.priorityEnd = a.priorityEnd();
    e.timeZone = a.timeZone();
    e.createdAt = a.createdAt();
  }
}
