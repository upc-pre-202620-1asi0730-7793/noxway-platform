package com.noctiva.noxway.moderation.infrastructure.persistence.jpa.entities;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "community_reports")
public class CommunityReportJpaEntity {
  @Id
  @Column(name = "id", nullable = false)
  public UUID id;

  @Column(name = "reported_by", nullable = false)
  public UUID reportedBy;

  @Column(name = "target_type", nullable = false, length = 32)
  public String targetType;

  @Column(name = "target_id", nullable = false)
  public UUID targetId;

  @Column(name = "reason", nullable = false, length = 1000)
  public String reason;

  @Column(name = "status", nullable = false, length = 32)
  public String status;

  @Column(name = "created_at", nullable = false)
  public Instant createdAt;

  @Version public long version;
}
