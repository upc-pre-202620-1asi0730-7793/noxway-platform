package com.noctiva.noxway.iam.infrastructure.persistence.jpa.assemblers;

import com.noctiva.noxway.iam.domain.model.aggregates.AuthSession;
import com.noctiva.noxway.iam.infrastructure.persistence.jpa.entities.AuthSessionPersistenceEntity;

public final class AuthSessionPersistenceAssembler {
  private AuthSessionPersistenceAssembler() {}

  public static AuthSession toDomain(AuthSessionPersistenceEntity e) {
    return AuthSession.reconstitute(
        e.getId(), e.getAccountId(), e.getTokenHash(), e.getExpiresAt(), !e.isActive(), e.getCreatedAt(), e.getVersion());
  }

  public static AuthSessionPersistenceEntity toPersistence(AuthSession s) {
    var e = new AuthSessionPersistenceEntity();
    e.setId(s.getId());
    e.setAccountId(s.getAccountId());
    e.setTokenHash(s.getTokenHash());
    e.setExpiresAt(s.getExpiresAt());
    e.setActive(!s.isRevoked());
    e.setCreatedAt(s.getCreatedAt());
    e.setVersion(s.getVersion());
    return e;
  }
}
