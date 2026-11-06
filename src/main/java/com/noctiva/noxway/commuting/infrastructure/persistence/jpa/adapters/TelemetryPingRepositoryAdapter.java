package com.noctiva.noxway.commuting.infrastructure.persistence.jpa.adapters;

import com.noctiva.noxway.commuting.domain.model.aggregates.TelemetryPing;
import com.noctiva.noxway.commuting.domain.repositories.TelemetryPingRepository;
import com.noctiva.noxway.commuting.infrastructure.persistence.jpa.entities.TelemetryPingJpaEntity;
import com.noctiva.noxway.commuting.infrastructure.persistence.jpa.mappers.TelemetryPingPersistenceMapper;
import com.noctiva.noxway.commuting.infrastructure.persistence.jpa.repositories.TelemetryPingJpaRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class TelemetryPingRepositoryAdapter implements TelemetryPingRepository {
  private final TelemetryPingJpaRepository jpa;

  public TelemetryPingRepositoryAdapter(TelemetryPingJpaRepository jpa) {
    this.jpa = jpa;
  }

  public Optional<TelemetryPing> findById(UUID id) {
    return jpa.findById(id).map(TelemetryPingPersistenceMapper::toDomain);
  }

  public java.util.List<TelemetryPing> findAll() {
    return jpa.findAll().stream().map(TelemetryPingPersistenceMapper::toDomain).toList();
  }

  public TelemetryPing save(TelemetryPing a) {
    var e = jpa.findById(a.id()).orElseGet(TelemetryPingJpaEntity::new);
    if (e.id != null && e.version != a.version())
      throw new org.springframework.orm.ObjectOptimisticLockingFailureException(
          TelemetryPing.class, a.id());
    TelemetryPingPersistenceMapper.copy(a, e);
    return TelemetryPingPersistenceMapper.toDomain(jpa.saveAndFlush(e));
  }

  public void delete(UUID id) {
    jpa.deleteById(id);
  }

  public java.util.List<TelemetryPing> findByCommuteId(UUID value) {
    return jpa.findByCommuteId(value).stream()
        .map(TelemetryPingPersistenceMapper::toDomain)
        .toList();
  }
}
