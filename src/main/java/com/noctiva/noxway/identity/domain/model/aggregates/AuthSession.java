package com.noctiva.noxway.identity.domain.model.aggregates;

import java.time.Instant;
import java.util.UUID;

public record AuthSession(
    UUID id,
    UUID accountId,
    String tokenHash,
    Instant expiresAt,
    boolean active,
    Instant createdAt,
    long version) {
  public static AuthSession create(
      UUID accountId, String tokenHash, Instant expiresAt, boolean active) {
    return new AuthSession(
        UUID.randomUUID(), accountId, tokenHash, expiresAt, active, Instant.now(), 0);
  }

  public AuthSession withAccountId(UUID value) {
    return new AuthSession(id, value, tokenHash, expiresAt, active, createdAt, version);
  }

  public AuthSession withTokenHash(String value) {
    return new AuthSession(id, accountId, value, expiresAt, active, createdAt, version);
  }

  public AuthSession withExpiresAt(Instant value) {
    return new AuthSession(id, accountId, tokenHash, value, active, createdAt, version);
  }

  public AuthSession withActive(boolean value) {
    return new AuthSession(id, accountId, tokenHash, expiresAt, value, createdAt, version);
  }
}
