package com.noctiva.noxway.identity.infrastructure.persistence.jpa.mappers;

import com.noctiva.noxway.identity.domain.model.aggregates.PasswordReset;
import com.noctiva.noxway.identity.infrastructure.persistence.jpa.entities.PasswordResetJpaEntity;

public final class PasswordResetPersistenceMapper {
  private PasswordResetPersistenceMapper() {}

  public static PasswordReset toDomain(PasswordResetJpaEntity e) {
    return new PasswordReset(
        e.id, e.accountId, e.tokenHash, e.expiresAt, e.used, e.createdAt, e.version);
  }

  public static void copy(PasswordReset a, PasswordResetJpaEntity e) {
    e.id = a.id();
    e.accountId = a.accountId();
    e.tokenHash = a.tokenHash();
    e.expiresAt = a.expiresAt();
    e.used = a.used();
    e.createdAt = a.createdAt();
  }
}
