package com.noctiva.noxway.identity.infrastructure.persistence.jpa.adapters;

import com.noctiva.noxway.identity.domain.model.aggregates.PasswordReset;
import com.noctiva.noxway.identity.domain.repositories.PasswordResetRepository;
import com.noctiva.noxway.identity.infrastructure.persistence.jpa.entities.PasswordResetJpaEntity;
import com.noctiva.noxway.identity.infrastructure.persistence.jpa.mappers.PasswordResetPersistenceMapper;
import com.noctiva.noxway.identity.infrastructure.persistence.jpa.repositories.PasswordResetJpaRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class PasswordResetRepositoryAdapter implements PasswordResetRepository {
  private final PasswordResetJpaRepository jpa;

  public PasswordResetRepositoryAdapter(PasswordResetJpaRepository jpa) {
    this.jpa = jpa;
  }

  public Optional<PasswordReset> findById(UUID id) {
    return jpa.findById(id).map(PasswordResetPersistenceMapper::toDomain);
  }

  public java.util.List<PasswordReset> findAll() {
    return jpa.findAll().stream().map(PasswordResetPersistenceMapper::toDomain).toList();
  }

  public PasswordReset save(PasswordReset a) {
    var e = jpa.findById(a.id()).orElseGet(PasswordResetJpaEntity::new);
    if (e.id != null && e.version != a.version())
      throw new org.springframework.orm.ObjectOptimisticLockingFailureException(
          PasswordReset.class, a.id());
    PasswordResetPersistenceMapper.copy(a, e);
    return PasswordResetPersistenceMapper.toDomain(jpa.saveAndFlush(e));
  }

  public void delete(UUID id) {
    jpa.deleteById(id);
  }

  public java.util.List<PasswordReset> findByAccountId(UUID value) {
    return jpa.findByAccountId(value).stream()
        .map(PasswordResetPersistenceMapper::toDomain)
        .toList();
  }

  public java.util.List<PasswordReset> findByTokenHash(String value) {
    return jpa.findByTokenHash(value).stream()
        .map(PasswordResetPersistenceMapper::toDomain)
        .toList();
  }
}
