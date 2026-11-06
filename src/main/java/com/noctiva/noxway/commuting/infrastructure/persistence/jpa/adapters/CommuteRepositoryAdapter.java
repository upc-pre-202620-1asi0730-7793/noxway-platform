package com.noctiva.noxway.commuting.infrastructure.persistence.jpa.adapters;

import com.noctiva.noxway.commuting.domain.model.aggregates.Commute;
import com.noctiva.noxway.commuting.domain.repositories.CommuteRepository;
import com.noctiva.noxway.commuting.infrastructure.persistence.jpa.entities.CommuteJpaEntity;
import com.noctiva.noxway.commuting.infrastructure.persistence.jpa.mappers.CommutePersistenceMapper;
import com.noctiva.noxway.commuting.infrastructure.persistence.jpa.repositories.CommuteJpaRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class CommuteRepositoryAdapter implements CommuteRepository {
  private final CommuteJpaRepository jpa;

  public CommuteRepositoryAdapter(CommuteJpaRepository jpa) {
    this.jpa = jpa;
  }

  public Optional<Commute> findById(UUID id) {
    return jpa.findById(id).map(CommutePersistenceMapper::toDomain);
  }

  public java.util.List<Commute> findAll() {
    return jpa.findAll().stream().map(CommutePersistenceMapper::toDomain).toList();
  }

  public Commute save(Commute a) {
    var e = jpa.findById(a.id()).orElseGet(CommuteJpaEntity::new);
    if (e.id != null && e.version != a.version())
      throw new org.springframework.orm.ObjectOptimisticLockingFailureException(
          Commute.class, a.id());
    CommutePersistenceMapper.copy(a, e);
    return CommutePersistenceMapper.toDomain(jpa.saveAndFlush(e));
  }

  public void delete(UUID id) {
    jpa.deleteById(id);
  }

  public java.util.List<Commute> findByWorkerId(UUID value) {
    return jpa.findByWorkerId(value).stream().map(CommutePersistenceMapper::toDomain).toList();
  }

  public java.util.List<Commute> findByStatus(String value) {
    return jpa.findByStatus(value).stream().map(CommutePersistenceMapper::toDomain).toList();
  }
}
