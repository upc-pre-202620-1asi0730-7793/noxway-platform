package com.noctiva.noxway.iam.infrastructure.persistence.jpa.assemblers;

import com.noctiva.noxway.iam.domain.model.aggregates.PasswordReset;
import com.noctiva.noxway.iam.infrastructure.persistence.jpa.entities.PasswordResetPersistenceEntity;

public final class PasswordResetPersistenceAssembler {
  private PasswordResetPersistenceAssembler() {}

  public static PasswordReset toDomain(PasswordResetPersistenceEntity e) {
    return PasswordReset.reconstitute(
        e.getId(), e.getAccountId(), e.getTokenHash(), e.getExpiresAt(), e.isUsed(), e.getCreatedAt(), e.getVersion());
  }

  public static PasswordResetPersistenceEntity toPersistence(PasswordReset r) {
    var e = new PasswordResetPersistenceEntity();
    e.setId(r.getId());
    e.setAccountId(r.getAccountId());
    e.setTokenHash(r.getTokenHash());
    e.setExpiresAt(r.getExpiresAt());
    e.setUsed(r.isUsed());
    e.setCreatedAt(r.getCreatedAt());
    e.setVersion(r.getVersion());
    return e;
  }
}
