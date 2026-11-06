package com.noctiva.noxway.wellness.infrastructure.persistence.jpa.adapters;

import com.noctiva.noxway.wellness.domain.model.aggregates.RestSession;
import com.noctiva.noxway.wellness.domain.repositories.RestSessionRepository;
import com.noctiva.noxway.wellness.infrastructure.persistence.jpa.entities.RestSessionJpaEntity;
import com.noctiva.noxway.wellness.infrastructure.persistence.jpa.mappers.RestSessionPersistenceMapper;
import com.noctiva.noxway.wellness.infrastructure.persistence.jpa.repositories.RestSessionJpaRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class RestSessionRepositoryAdapter implements RestSessionRepository {
  private final RestSessionJpaRepository jpa;

  public RestSessionRepositoryAdapter(RestSessionJpaRepository jpa) {
    this.jpa = jpa;
  }

  public Optional<RestSession> findById(UUID id) {
    return jpa.findById(id).map(RestSessionPersistenceMapper::toDomain);
  }

  public java.util.List<RestSession> findAll() {
    return jpa.findAll().stream().map(RestSessionPersistenceMapper::toDomain).toList();
  }

  public RestSession save(RestSession a) {
    var e = jpa.findById(a.id()).orElseGet(RestSessionJpaEntity::new);
    if (e.id != null && e.version != a.version())
      throw new org.springframework.orm.ObjectOptimisticLockingFailureException(
          RestSession.class, a.id());
    RestSessionPersistenceMapper.copy(a, e);
    return RestSessionPersistenceMapper.toDomain(jpa.saveAndFlush(e));
  }

  public void delete(UUID id) {
    jpa.deleteById(id);
  }

  public java.util.List<RestSession> findByWorkerId(UUID value) {
    return jpa.findByWorkerId(value).stream().map(RestSessionPersistenceMapper::toDomain).toList();
  }
}
