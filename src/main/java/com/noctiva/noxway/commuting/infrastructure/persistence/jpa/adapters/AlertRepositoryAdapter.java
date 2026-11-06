package com.noctiva.noxway.commuting.infrastructure.persistence.jpa.adapters;

import com.noctiva.noxway.commuting.domain.model.aggregates.Alert;
import com.noctiva.noxway.commuting.domain.repositories.AlertRepository;
import com.noctiva.noxway.commuting.infrastructure.persistence.jpa.entities.AlertJpaEntity;
import com.noctiva.noxway.commuting.infrastructure.persistence.jpa.mappers.AlertPersistenceMapper;
import com.noctiva.noxway.commuting.infrastructure.persistence.jpa.repositories.AlertJpaRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class AlertRepositoryAdapter implements AlertRepository {
  private final AlertJpaRepository jpa;

  public AlertRepositoryAdapter(AlertJpaRepository jpa) {
    this.jpa = jpa;
  }

  public Optional<Alert> findById(UUID id) {
    return jpa.findById(id).map(AlertPersistenceMapper::toDomain);
  }

  public java.util.List<Alert> findAll() {
    return jpa.findAll().stream().map(AlertPersistenceMapper::toDomain).toList();
  }

  public Alert save(Alert a) {
    var e = jpa.findById(a.id()).orElseGet(AlertJpaEntity::new);
    if (e.id != null && e.version != a.version())
      throw new org.springframework.orm.ObjectOptimisticLockingFailureException(
          Alert.class, a.id());
    AlertPersistenceMapper.copy(a, e);
    return AlertPersistenceMapper.toDomain(jpa.saveAndFlush(e));
  }

  public void delete(UUID id) {
    jpa.deleteById(id);
  }

  public java.util.List<Alert> findByCommuteId(UUID value) {
    return jpa.findByCommuteId(value).stream().map(AlertPersistenceMapper::toDomain).toList();
  }

  public java.util.List<Alert> findByIncidentId(UUID value) {
    return jpa.findByIncidentId(value).stream().map(AlertPersistenceMapper::toDomain).toList();
  }

  public java.util.List<Alert> findByContactId(UUID value) {
    return jpa.findByContactId(value).stream().map(AlertPersistenceMapper::toDomain).toList();
  }

  public java.util.List<Alert> findByStatus(String value) {
    return jpa.findByStatus(value).stream().map(AlertPersistenceMapper::toDomain).toList();
  }
}
