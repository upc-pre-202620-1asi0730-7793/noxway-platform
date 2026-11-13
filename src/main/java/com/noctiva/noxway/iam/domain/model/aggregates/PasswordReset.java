package com.noctiva.noxway.iam.domain.model.aggregates;

import com.noctiva.noxway.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import java.time.Instant;
import java.util.UUID;

/**
 * Domain aggregate structuring single-use password recovery tokens (reserved for IAM implementation).
 */
public class PasswordReset extends AbstractDomainAggregateRoot<PasswordReset> {

  private final UUID id;
  private final UUID accountId;
  private final String tokenHash;
  private final Instant expiresAt;
  private boolean used;
  private final Instant createdAt;
  private final long version;

  private PasswordReset(
      UUID id,
      UUID accountId,
      String tokenHash,
      Instant expiresAt,
      boolean used,
      Instant createdAt,
      long version) {
    this.id = id;
    this.accountId = accountId;
    this.tokenHash = tokenHash;
    this.expiresAt = expiresAt;
    this.used = used;
    this.createdAt = createdAt;
    this.version = version;
  }

  public static PasswordReset create(UUID accountId, String tokenHash, Instant expiresAt) {
    return new PasswordReset(
        UUID.randomUUID(), accountId, tokenHash, expiresAt, false, Instant.now(), 0L);
  }

  public static PasswordReset reconstitute(
      UUID id,
      UUID accountId,
      String tokenHash,
      Instant expiresAt,
      boolean used,
      Instant createdAt,
      long version) {
    return new PasswordReset(id, accountId, tokenHash, expiresAt, used, createdAt, version);
  }

  public void markUsed() {
    this.used = true;
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

  public boolean isUsed() {
    return used;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public long getVersion() {
    return version;
  }
}
