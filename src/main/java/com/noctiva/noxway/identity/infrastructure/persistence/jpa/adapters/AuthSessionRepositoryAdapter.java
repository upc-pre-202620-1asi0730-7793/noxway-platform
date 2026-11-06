package com.noctiva.noxway.identity.infrastructure.persistence.jpa.adapters;

import com.noctiva.noxway.identity.domain.model.aggregates.AuthSession;
import com.noctiva.noxway.identity.domain.repositories.AuthSessionRepository;
import com.noctiva.noxway.identity.infrastructure.persistence.jpa.entities.AuthSessionJpaEntity;
import com.noctiva.noxway.identity.infrastructure.persistence.jpa.mappers.AuthSessionPersistenceMapper;
import com.noctiva.noxway.identity.infrastructure.persistence.jpa.repositories.AuthSessionJpaRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class AuthSessionRepositoryAdapter implements AuthSessionRepository {
  private final AuthSessionJpaRepository jpa;

  public AuthSessionRepositoryAdapter(AuthSessionJpaRepository jpa) {
    this.jpa = jpa;
  }

  public Optional<AuthSession> findById(UUID id) {
    return jpa.findById(id).map(AuthSessionPersistenceMapper::toDomain);
  }

  public java.util.List<AuthSession> findAll() {
    return jpa.findAll().stream().map(AuthSessionPersistenceMapper::toDomain).toList();
  }

  public AuthSession save(AuthSession a) {
    var e = jpa.findById(a.id()).orElseGet(AuthSessionJpaEntity::new);
    if (e.id != null && e.version != a.version())
      throw new org.springframework.orm.ObjectOptimisticLockingFailureException(
          AuthSession.class, a.id());
    AuthSessionPersistenceMapper.copy(a, e);
    return AuthSessionPersistenceMapper.toDomain(jpa.saveAndFlush(e));
  }

  public void delete(UUID id) {
    jpa.deleteById(id);
  }

  public java.util.List<AuthSession> findByAccountId(UUID value) {
    return jpa.findByAccountId(value).stream().map(AuthSessionPersistenceMapper::toDomain).toList();
  }

  public java.util.List<AuthSession> findByTokenHash(String value) {
    return jpa.findByTokenHash(value).stream().map(AuthSessionPersistenceMapper::toDomain).toList();
  }
}
