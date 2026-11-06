package com.noctiva.noxway.commuting.infrastructure.persistence.jpa.entities;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "alerts", schema = "commute_incident_schema")
public class AlertJpaEntity {
  @Id
  @Column(name = "id", nullable = false)
  public UUID id;

  @Column(name = "commute_id", nullable = false)
  public UUID commuteId;

  @Column(name = "incident_id", nullable = true)
  public UUID incidentId;

  @Column(name = "contact_id", nullable = false)
  public UUID contactId;

  @Column(name = "channel", nullable = false)
  public String channel;

  @Column(name = "status", nullable = false)
  public String status;

  @Column(name = "message", nullable = false)
  public String message;

  @Column(name = "delivered_at", nullable = true)
  public Instant deliveredAt;

  @Column(name = "created_at", nullable = false)
  public Instant createdAt;

  @Version public long version;
}
