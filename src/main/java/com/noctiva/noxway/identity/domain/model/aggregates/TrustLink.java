package com.noctiva.noxway.identity.domain.model.aggregates;

import java.time.Instant;
import java.util.UUID;

public record TrustLink(
    UUID id,
    UUID workerId,
    UUID contactId,
    String status,
    Instant acceptedAt,
    Instant createdAt,
    long version) {
  public static TrustLink create(UUID workerId, UUID contactId, String status, Instant acceptedAt) {
    return new TrustLink(
        UUID.randomUUID(), workerId, contactId, status, acceptedAt, Instant.now(), 0);
  }

  public TrustLink withWorkerId(UUID value) {
    return new TrustLink(id, value, contactId, status, acceptedAt, createdAt, version);
  }

  public TrustLink withContactId(UUID value) {
    return new TrustLink(id, workerId, value, status, acceptedAt, createdAt, version);
  }

  public TrustLink withStatus(String value) {
    return new TrustLink(id, workerId, contactId, value, acceptedAt, createdAt, version);
  }

  public TrustLink withAcceptedAt(Instant value) {
    return new TrustLink(id, workerId, contactId, status, value, createdAt, version);
  }
}
