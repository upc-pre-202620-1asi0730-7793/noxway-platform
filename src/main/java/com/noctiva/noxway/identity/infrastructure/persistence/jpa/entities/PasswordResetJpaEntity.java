package com.noctiva.noxway.identity.infrastructure.persistence.jpa.entities;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "password_resets")
public class PasswordResetJpaEntity {
  @Id
  @Column(name = "id", nullable = false)
  public UUID id;

  @Column(name = "account_id", nullable = false)
  public UUID accountId;

  @Column(name = "token_hash", nullable = false, unique = true, length = 64)
  public String tokenHash;

  @Column(name = "expires_at", nullable = false)
  public Instant expiresAt;

  @Column(name = "used", nullable = false)
  public boolean used;

  @Column(name = "created_at", nullable = false)
  public Instant createdAt;

  @Version public long version;
}
