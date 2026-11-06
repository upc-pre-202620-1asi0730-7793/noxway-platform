package com.noctiva.noxway.identity.domain.model.aggregates;

import java.time.Instant;
import java.util.UUID;

public record PasswordReset(
    UUID id,
    UUID accountId,
    String tokenHash,
    Instant expiresAt,
    boolean used,
    Instant createdAt,
    long version) {
  public static PasswordReset create(
      UUID accountId, String tokenHash, Instant expiresAt, boolean used) {
    return new PasswordReset(
        UUID.randomUUID(), accountId, tokenHash, expiresAt, used, Instant.now(), 0);
  }

  public PasswordReset withAccountId(UUID value) {
    return new PasswordReset(id, value, tokenHash, expiresAt, used, createdAt, version);
  }

  public PasswordReset withTokenHash(String value) {
    return new PasswordReset(id, accountId, value, expiresAt, used, createdAt, version);
  }

  public PasswordReset withExpiresAt(Instant value) {
    return new PasswordReset(id, accountId, tokenHash, value, used, createdAt, version);
  }

  public PasswordReset withUsed(boolean value) {
    return new PasswordReset(id, accountId, tokenHash, expiresAt, value, createdAt, version);
  }
}
