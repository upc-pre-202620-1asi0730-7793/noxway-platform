package com.noctiva.noxway.moderation.infrastructure.persistence.jpa.entities;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "moderation_actions", schema = "moderation_schema")
public class ModerationActionJpaEntity {
  @Id
  @Column(name = "id", nullable = false)
  public UUID id;

  @Column(name = "report_id", nullable = false)
  public UUID reportId;

  @Column(name = "moderator_id", nullable = false)
  public UUID moderatorId;

  @Column(name = "decision", nullable = false)
  public String decision;

  @Column(name = "notes", nullable = false)
  public String notes;

  @Column(name = "created_at", nullable = false)
  public Instant createdAt;

  @Version public long version;
}
