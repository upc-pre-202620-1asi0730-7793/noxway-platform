package com.noctiva.noxway.identity.infrastructure.persistence.jpa.entities;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "notification_preferences")
public class NotificationPreferenceJpaEntity {
  @Id
  @Column(name = "id", nullable = false)
  public UUID id;

  @Column(name = "account_id", nullable = false)
  public UUID accountId;

  @Column(name = "channel", nullable = false, length = 16)
  public String channel;

  @Column(name = "enabled", nullable = false)
  public boolean enabled;

  @Column(name = "priority_start", nullable = false, length = 5)
  public String priorityStart;

  @Column(name = "priority_end", nullable = false, length = 5)
  public String priorityEnd;

  @Column(name = "time_zone", nullable = false, length = 100)
  public String timeZone;

  @Column(name = "created_at", nullable = false)
  public Instant createdAt;

  @Version public long version;
}
