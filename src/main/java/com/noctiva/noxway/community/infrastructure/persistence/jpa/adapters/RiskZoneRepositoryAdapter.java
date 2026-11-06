package com.noctiva.noxway.community.infrastructure.persistence.jpa.adapters;

import com.noctiva.noxway.community.domain.model.aggregates.RiskZone;
import com.noctiva.noxway.community.domain.repositories.RiskZoneRepository;
import com.noctiva.noxway.community.infrastructure.persistence.jpa.entities.RiskZoneJpaEntity;
import com.noctiva.noxway.community.infrastructure.persistence.jpa.mappers.RiskZonePersistenceMapper;
import com.noctiva.noxway.community.infrastructure.persistence.jpa.repositories.RiskZoneJpaRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class RiskZoneRepositoryAdapter implements RiskZoneRepository {
  private final RiskZoneJpaRepository jpa;

  public RiskZoneRepositoryAdapter(RiskZoneJpaRepository jpa) {
    this.jpa = jpa;
  }

  public Optional<RiskZone> findById(UUID id) {
    return jpa.findById(id).map(RiskZonePersistenceMapper::toDomain);
  }

  public java.util.List<RiskZone> findAll() {
    return jpa.findAll().stream().map(RiskZonePersistenceMapper::toDomain).toList();
  }

  public RiskZone save(RiskZone a) {
    var e = jpa.findById(a.id()).orElseGet(RiskZoneJpaEntity::new);
    if (e.id != null && e.version != a.version())
      throw new org.springframework.orm.ObjectOptimisticLockingFailureException(
          RiskZone.class, a.id());
    RiskZonePersistenceMapper.copy(a, e);
    return RiskZonePersistenceMapper.toDomain(jpa.saveAndFlush(e));
  }

  public void delete(UUID id) {
    jpa.deleteById(id);
  }

  public java.util.List<RiskZone> findByReportedBy(UUID value) {
    return jpa.findByReportedBy(value).stream().map(RiskZonePersistenceMapper::toDomain).toList();
  }

  public java.util.List<RiskZone> findByStatus(String value) {
    return jpa.findByStatus(value).stream().map(RiskZonePersistenceMapper::toDomain).toList();
  }
}
