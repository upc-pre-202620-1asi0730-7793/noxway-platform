package com.noctiva.noxway.profiles.domain.model.aggregates;

import static com.noctiva.noxway.shared.domain.Rules.require;

import com.noctiva.noxway.shared.domain.RuleViolation;
import com.noctiva.noxway.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Set;
import java.util.UUID;

/**
 * NotificationPreference aggregate root governing how an account receives commute and emergency alerts.
 */
public class NotificationPreference extends AbstractDomainAggregateRoot<NotificationPreference> {

  private static final Set<String> CHANNELS = Set.of("EMAIL", "SMS", "WHATSAPP");

  private final UUID id;
  private final UUID accountId;
  private final String channel;
  private boolean enabled;
  private String priorityStart;
  private String priorityEnd;
  private String timeZone;
  private final Instant createdAt;
  private final long version;

  private NotificationPreference(
      UUID id,
      UUID accountId,
      String channel,
      boolean enabled,
      String priorityStart,
      String priorityEnd,
      String timeZone,
      Instant createdAt,
      long version) {
    this.id = id;
    this.accountId = accountId;
    this.channel = channel;
    this.enabled = enabled;
    this.priorityStart = priorityStart;
    this.priorityEnd = priorityEnd;
    this.timeZone = timeZone;
    this.createdAt = createdAt;
    this.version = version;
  }

  public static NotificationPreference create(
      UUID accountId,
      String channel,
      boolean enabled,
      String priorityStart,
      String priorityEnd,
      String timeZone) {
    require(channel != null && CHANNELS.contains(channel), "Invalid notification channel");
    validateTime(priorityStart);
    validateTime(priorityEnd);
    validateZone(timeZone);
    return new NotificationPreference(
        UUID.randomUUID(),
        accountId,
        channel,
        enabled,
        priorityStart,
        priorityEnd,
        timeZone,
        Instant.now(),
        0L);
  }

  public static NotificationPreference reconstitute(
      UUID id,
      UUID accountId,
      String channel,
      boolean enabled,
      String priorityStart,
      String priorityEnd,
      String timeZone,
      Instant createdAt,
      long version) {
    return new NotificationPreference(
        id, accountId, channel, enabled, priorityStart, priorityEnd, timeZone, createdAt, version);
  }

  public void update(boolean enabled, String priorityStart, String priorityEnd, String timeZone) {
    validateTime(priorityStart);
    validateTime(priorityEnd);
    validateZone(timeZone);
    this.enabled = enabled;
    this.priorityStart = priorityStart;
    this.priorityEnd = priorityEnd;
    this.timeZone = timeZone;
  }

  private static void validateTime(String value) {
    require(value != null && !value.isBlank(), "Priority window time is required");
    try {
      LocalTime.parse(value);
    } catch (Exception ex) {
      throw new RuleViolation("Invalid time format: " + value);
    }
  }

  private static void validateZone(String value) {
    require(value != null && !value.isBlank(), "Time zone is required");
    try {
      ZoneId.of(value);
    } catch (Exception ex) {
      throw new RuleViolation("Invalid time zone: " + value);
    }
  }

  public UUID getId() {
    return id;
  }

  public UUID getAccountId() {
    return accountId;
  }

  public String getChannel() {
    return channel;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public String getPriorityStart() {
    return priorityStart;
  }

  public String getPriorityEnd() {
    return priorityEnd;
  }

  public String getTimeZone() {
    return timeZone;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public long getVersion() {
    return version;
  }
}
