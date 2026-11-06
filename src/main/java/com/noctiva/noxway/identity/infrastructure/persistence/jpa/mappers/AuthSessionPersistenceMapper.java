package com.noctiva.noxway.identity.infrastructure.persistence.jpa.mappers;

import com.noctiva.noxway.identity.domain.model.aggregates.AuthSession;
import com.noctiva.noxway.identity.infrastructure.persistence.jpa.entities.AuthSessionJpaEntity;

public final class AuthSessionPersistenceMapper {
  private AuthSessionPersistenceMapper() {}

  public static AuthSession toDomain(AuthSessionJpaEntity e) {
    return new AuthSession(
        e.id, e.accountId, e.tokenHash, e.expiresAt, e.active, e.createdAt, e.version);
  }

  public static void copy(AuthSession a, AuthSessionJpaEntity e) {
    e.id = a.id();
    e.accountId = a.accountId();
    e.tokenHash = a.tokenHash();
    e.expiresAt = a.expiresAt();
    e.active = a.active();
    e.createdAt = a.createdAt();
  }
}
