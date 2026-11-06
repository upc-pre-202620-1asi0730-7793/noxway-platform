package com.noctiva.noxway.commuting.infrastructure.persistence.jpa.adapters;

import com.noctiva.noxway.commuting.domain.model.aggregates.SafetyIncident;
import com.noctiva.noxway.commuting.domain.repositories.SafetyIncidentRepository;
import com.noctiva.noxway.commuting.infrastructure.persistence.jpa.entities.SafetyIncidentJpaEntity;
import com.noctiva.noxway.commuting.infrastructure.persistence.jpa.mappers.SafetyIncidentPersistenceMapper;
import com.noctiva.noxway.commuting.infrastructure.persistence.jpa.repositories.SafetyIncidentJpaRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class SafetyIncidentRepositoryAdapter implements SafetyIncidentRepository {
  private final SafetyIncidentJpaRepository jpa;

  public SafetyIncidentRepositoryAdapter(SafetyIncidentJpaRepository jpa) {
    this.jpa = jpa;
  }

  public Optional<SafetyIncident> findById(UUID id) {
    return jpa.findById(id).map(SafetyIncidentPersistenceMapper::toDomain);
  }

  public java.util.List<SafetyIncident> findAll() {
    return jpa.findAll().stream().map(SafetyIncidentPersistenceMapper::toDomain).toList();
  }

  public SafetyIncident save(SafetyIncident a) {
    var e = jpa.findById(a.id()).orElseGet(SafetyIncidentJpaEntity::new);
    if (e.id != null && e.version != a.version())
      throw new org.springframework.orm.ObjectOptimisticLockingFailureException(
          SafetyIncident.class, a.id());
    SafetyIncidentPersistenceMapper.copy(a, e);
    return SafetyIncidentPersistenceMapper.toDomain(jpa.saveAndFlush(e));
  }

  public void delete(UUID id) {
    jpa.deleteById(id);
  }

  public java.util.List<SafetyIncident> findByCommuteId(UUID value) {
    return jpa.findByCommuteId(value).stream()
        .map(SafetyIncidentPersistenceMapper::toDomain)
        .toList();
  }

  public java.util.List<SafetyIncident> findByWorkerId(UUID value) {
    return jpa.findByWorkerId(value).stream()
        .map(SafetyIncidentPersistenceMapper::toDomain)
        .toList();
  }

  public java.util.List<SafetyIncident> findByStatus(String value) {
    return jpa.findByStatus(value).stream().map(SafetyIncidentPersistenceMapper::toDomain).toList();
  }
}
