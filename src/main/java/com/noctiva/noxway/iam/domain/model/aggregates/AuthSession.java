package com.noctiva.noxway.iam.domain.model.aggregates;

import com.noctiva.noxway.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import java.time.Instant;
import java.util.UUID;

/**
 * Domain aggregate structuring an authentication session (reserved for full IAM implementation).
 */
public class AuthSession extends AbstractDomainAggregateRoot<AuthSession> {

  private final UUID id;
  private final UUID accountId;
  private final String tokenHash;
  private final Instant expiresAt;
  private boolean revoked;
  private final Instant createdAt;
  private final long version;

  private AuthSession(
      UUID id,
      UUID accountId,
      String tokenHash,
      Instant expiresAt,
      boolean revoked,
      Instant createdAt,
      long version) {
    this.id = id;
    this.accountId = accountId;
    this.tokenHash = tokenHash;
    this.expiresAt = expiresAt;
    this.revoked = revoked;
    this.createdAt = createdAt;
    this.version = version;
  }

  public static AuthSession create(UUID accountId, String tokenHash, Instant expiresAt) {
    return new AuthSession(
        UUID.randomUUID(), accountId, tokenHash, expiresAt, false, Instant.now(), 0L);
  }

  public static AuthSession reconstitute(
      UUID id,
      UUID accountId,
      String tokenHash,
      Instant expiresAt,
      boolean revoked,
      Instant createdAt,
      long version) {
    return new AuthSession(id, accountId, tokenHash, expiresAt, revoked, createdAt, version);
  }

  public void revoke() {
    this.revoked = true;
  }

  public UUID getId() {
    return id;
  }

  public UUID getAccountId() {
    return accountId;
  }

  public String getTokenHash() {
    return tokenHash;
  }

  public Instant getExpiresAt() {
    return expiresAt;
  }

  public boolean isRevoked() {
    return revoked;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public long getVersion() {
    return version;
  }
}
