package com.noctiva.noxway.community.infrastructure.persistence.jpa.entities;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "service_votes")
public class ServiceVoteJpaEntity {
  @Id
  @Column(name = "id", nullable = false)
  public UUID id;

  @Column(name = "service_id", nullable = false)
  public UUID serviceId;

  @Column(name = "account_id", nullable = false)
  public UUID accountId;

  @Column(name = "direction", nullable = false)
  public int direction;

  @Column(name = "created_at", nullable = false)
  public Instant createdAt;

  @Version public long version;
}
