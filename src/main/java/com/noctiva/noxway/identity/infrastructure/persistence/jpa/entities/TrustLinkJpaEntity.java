package com.noctiva.noxway.identity.infrastructure.persistence.jpa.entities;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "trust_links")
public class TrustLinkJpaEntity {
  @Id
  @Column(name = "id", nullable = false)
  public UUID id;

  @Column(name = "worker_id", nullable = false)
  public UUID workerId;

  @Column(name = "contact_id", nullable = false)
  public UUID contactId;

  @Column(name = "status", nullable = false, length = 32)
  public String status;

  @Column(name = "accepted_at", nullable = true)
  public Instant acceptedAt;

  @Column(name = "created_at", nullable = false)
  public Instant createdAt;

  @Version public long version;
}
