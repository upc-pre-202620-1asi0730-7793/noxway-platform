package com.noctiva.noxway.identity.infrastructure.persistence.jpa.adapters;

import com.noctiva.noxway.identity.domain.model.aggregates.Account;
import com.noctiva.noxway.identity.domain.repositories.AccountRepository;
import com.noctiva.noxway.identity.infrastructure.persistence.jpa.entities.AccountJpaEntity;
import com.noctiva.noxway.identity.infrastructure.persistence.jpa.mappers.AccountPersistenceMapper;
import com.noctiva.noxway.identity.infrastructure.persistence.jpa.repositories.AccountJpaRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class AccountRepositoryAdapter implements AccountRepository {
  private final AccountJpaRepository jpa;

  public AccountRepositoryAdapter(AccountJpaRepository jpa) {
    this.jpa = jpa;
  }

  public Optional<Account> lockById(UUID id) {
    return jpa.lockById(id).map(AccountPersistenceMapper::toDomain);
  }

  public Optional<Account> findById(UUID id) {
    return jpa.findById(id).map(AccountPersistenceMapper::toDomain);
  }

  public java.util.List<Account> findAll() {
    return jpa.findAll().stream().map(AccountPersistenceMapper::toDomain).toList();
  }

  public Account save(Account a) {
    var e = jpa.findById(a.id()).orElseGet(AccountJpaEntity::new);
    if (e.id != null && e.version != a.version())
      throw new org.springframework.orm.ObjectOptimisticLockingFailureException(
          Account.class, a.id());
    AccountPersistenceMapper.copy(a, e);
    return AccountPersistenceMapper.toDomain(jpa.saveAndFlush(e));
  }

  public void delete(UUID id) {
    jpa.deleteById(id);
  }

  public java.util.List<Account> findByEmail(String value) {
    return jpa.findByEmail(value).stream().map(AccountPersistenceMapper::toDomain).toList();
  }
}
