package com.noctiva.noxway.profiles.infrastructure.persistence.jpa.assemblers;

import com.noctiva.noxway.profiles.domain.model.aggregates.AccountProfile;
import com.noctiva.noxway.profiles.infrastructure.persistence.jpa.entities.AccountPersistenceEntity;

public final class AccountPersistenceAssembler {
  private AccountPersistenceAssembler() {}

  public static AccountProfile toDomain(AccountPersistenceEntity e) {
    return AccountProfile.reconstitute(
        e.getId(),
        e.getEmail(),
        e.getFullName(),
        e.getPhone(),
        e.getRole(),
        e.getShiftType(),
        e.getShiftStart(),
        e.getShiftEnd(),
        e.getTimeZone(),
        e.getPasswordHash(),
        e.isActive(),
        e.getCreatedAt(),
        e.getVersion());
  }

  public static AccountPersistenceEntity toPersistence(AccountProfile a) {
    var e = new AccountPersistenceEntity();
    e.setId(a.getId());
    e.setEmail(a.getEmail());
    e.setFullName(a.getFullName());
    e.setPhone(a.getPhone());
    e.setRole(a.getRole());
    e.setShiftType(a.getShiftType());
    e.setShiftStart(a.getShiftStart());
    e.setShiftEnd(a.getShiftEnd());
    e.setTimeZone(a.getTimeZone());
    e.setPasswordHash(a.getPasswordHash());
    e.setActive(a.isActive());
    e.setCreatedAt(a.getCreatedAt());
    e.setVersion(a.getVersion());
    return e;
  }
}
