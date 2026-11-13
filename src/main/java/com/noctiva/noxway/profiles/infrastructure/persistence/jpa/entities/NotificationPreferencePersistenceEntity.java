package com.noctiva.noxway.profiles.infrastructure.persistence.jpa.entities;

import com.noctiva.noxway.shared.infrastructure.persistence.jpa.entities.AuditableUuidPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "notification_preferences", schema = "users_network_schema")
public class NotificationPreferencePersistenceEntity extends AuditableUuidPersistenceEntity {

  @Column(name = "account_id", nullable = false)
  private UUID accountId;

  @Column(name = "channel", nullable = false)
  private String channel;

  @Column(name = "enabled", nullable = false)
  private boolean enabled;

  @Column(name = "priority_start", nullable = false)
  private String priorityStart;

  @Column(name = "priority_end", nullable = false)
  private String priorityEnd;

  @Column(name = "time_zone", nullable = false)
  private String timeZone;

  public NotificationPreferencePersistenceEntity() {}

  public UUID getAccountId() {
    return accountId;
  }

  public void setAccountId(UUID accountId) {
    this.accountId = accountId;
  }

  public String getChannel() {
    return channel;
  }

  public void setChannel(String channel) {
    this.channel = channel;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public String getPriorityStart() {
    return priorityStart;
  }

  public void setPriorityStart(String priorityStart) {
    this.priorityStart = priorityStart;
  }

  public String getPriorityEnd() {
    return priorityEnd;
  }

  public void setPriorityEnd(String priorityEnd) {
    this.priorityEnd = priorityEnd;
  }

  public String getTimeZone() {
    return timeZone;
  }

  public void setTimeZone(String timeZone) {
    this.timeZone = timeZone;
  }
}
