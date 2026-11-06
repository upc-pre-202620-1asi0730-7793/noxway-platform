package com.noctiva.noxway.identity.domain.model.aggregates;

import java.time.Instant;
import java.util.UUID;

public record NotificationPreference(
    UUID id,
    UUID accountId,
    String channel,
    boolean enabled,
    String priorityStart,
    String priorityEnd,
    String timeZone,
    Instant createdAt,
    long version) {
  public static NotificationPreference create(
      UUID accountId,
      String channel,
      boolean enabled,
      String priorityStart,
      String priorityEnd,
      String timeZone) {
    return new NotificationPreference(
        UUID.randomUUID(),
        accountId,
        channel,
        enabled,
        priorityStart,
        priorityEnd,
        timeZone,
        Instant.now(),
        0);
  }

  public NotificationPreference withAccountId(UUID value) {
    return new NotificationPreference(
        id, value, channel, enabled, priorityStart, priorityEnd, timeZone, createdAt, version);
  }

  public NotificationPreference withChannel(String value) {
    return new NotificationPreference(
        id, accountId, value, enabled, priorityStart, priorityEnd, timeZone, createdAt, version);
  }

  public NotificationPreference withEnabled(boolean value) {
    return new NotificationPreference(
        id, accountId, channel, value, priorityStart, priorityEnd, timeZone, createdAt, version);
  }

  public NotificationPreference withPriorityStart(String value) {
    return new NotificationPreference(
        id, accountId, channel, enabled, value, priorityEnd, timeZone, createdAt, version);
  }

  public NotificationPreference withPriorityEnd(String value) {
    return new NotificationPreference(
        id, accountId, channel, enabled, priorityStart, value, timeZone, createdAt, version);
  }

  public NotificationPreference withTimeZone(String value) {
    return new NotificationPreference(
        id, accountId, channel, enabled, priorityStart, priorityEnd, value, createdAt, version);
  }
}
