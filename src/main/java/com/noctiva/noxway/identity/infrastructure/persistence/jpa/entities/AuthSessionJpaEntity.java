package com.noctiva.noxway.identity.infrastructure.persistence.jpa.entities;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "auth_sessions")
public class AuthSessionJpaEntity {
  @Id
  @Column(name = "id", nullable = false)
  public UUID id;

  @Column(name = "account_id", nullable = false)
  public UUID accountId;

  @Column(name = "token_hash", nullable = false, unique = true, length = 64)
  public String tokenHash;

  @Column(name = "expires_at", nullable = false)
  public Instant expiresAt;

  @Column(name = "active", nullable = false)
  public boolean active;

  @Column(name = "created_at", nullable = false)
  public Instant createdAt;

  @Version public long version;
}
