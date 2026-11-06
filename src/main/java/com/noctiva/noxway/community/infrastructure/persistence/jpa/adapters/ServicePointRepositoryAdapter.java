package com.noctiva.noxway.community.infrastructure.persistence.jpa.adapters;

import com.noctiva.noxway.community.domain.model.aggregates.ServicePoint;
import com.noctiva.noxway.community.domain.repositories.ServicePointRepository;
import com.noctiva.noxway.community.infrastructure.persistence.jpa.entities.ServicePointJpaEntity;
import com.noctiva.noxway.community.infrastructure.persistence.jpa.mappers.ServicePointPersistenceMapper;
import com.noctiva.noxway.community.infrastructure.persistence.jpa.repositories.ServicePointJpaRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class ServicePointRepositoryAdapter implements ServicePointRepository {
  private final ServicePointJpaRepository jpa;

  public ServicePointRepositoryAdapter(ServicePointJpaRepository jpa) {
    this.jpa = jpa;
  }

  public Optional<ServicePoint> findById(UUID id) {
    return jpa.findById(id).map(ServicePointPersistenceMapper::toDomain);
  }

  public java.util.List<ServicePoint> findAll() {
    return jpa.findAll().stream().map(ServicePointPersistenceMapper::toDomain).toList();
  }

  public ServicePoint save(ServicePoint a) {
    var e = jpa.findById(a.id()).orElseGet(ServicePointJpaEntity::new);
    if (e.id != null && e.version != a.version())
      throw new org.springframework.orm.ObjectOptimisticLockingFailureException(
          ServicePoint.class, a.id());
    ServicePointPersistenceMapper.copy(a, e);
    return ServicePointPersistenceMapper.toDomain(jpa.saveAndFlush(e));
  }

  public void delete(UUID id) {
    jpa.deleteById(id);
  }

  public java.util.List<ServicePoint> findByReportedBy(UUID value) {
    return jpa.findByReportedBy(value).stream()
        .map(ServicePointPersistenceMapper::toDomain)
        .toList();
  }

  public java.util.List<ServicePoint> findByStatus(String value) {
    return jpa.findByStatus(value).stream().map(ServicePointPersistenceMapper::toDomain).toList();
  }
}
