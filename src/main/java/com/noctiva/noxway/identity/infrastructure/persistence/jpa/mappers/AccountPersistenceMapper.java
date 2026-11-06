package com.noctiva.noxway.identity.infrastructure.persistence.jpa.mappers;

import com.noctiva.noxway.identity.domain.model.aggregates.Account;
import com.noctiva.noxway.identity.infrastructure.persistence.jpa.entities.AccountJpaEntity;

public final class AccountPersistenceMapper {
  private AccountPersistenceMapper() {}

  public static Account toDomain(AccountJpaEntity e) {
    return new Account(
        e.id,
        e.email,
        e.fullName,
        e.phone,
        e.role,
        e.shiftType,
        e.shiftStart,
        e.shiftEnd,
        e.timeZone,
        e.passwordHash,
        e.active,
        e.createdAt,
        e.version);
  }

  public static void copy(Account a, AccountJpaEntity e) {
    e.id = a.id();
    e.email = a.email();
    e.fullName = a.fullName();
    e.phone = a.phone();
    e.role = a.role();
    e.shiftType = a.shiftType();
    e.shiftStart = a.shiftStart();
    e.shiftEnd = a.shiftEnd();
    e.timeZone = a.timeZone();
    e.passwordHash = a.passwordHash();
    e.active = a.active();
    e.createdAt = a.createdAt();
  }
}
