package com.noctiva.noxway.community.infrastructure.persistence.jpa.adapters;

import com.noctiva.noxway.community.domain.model.aggregates.ServiceOpeningInterval;
import com.noctiva.noxway.community.domain.repositories.ServiceOpeningIntervalRepository;
import com.noctiva.noxway.community.infrastructure.persistence.jpa.entities.ServiceOpeningIntervalJpaEntity;
import com.noctiva.noxway.community.infrastructure.persistence.jpa.mappers.ServiceOpeningIntervalPersistenceMapper;
import com.noctiva.noxway.community.infrastructure.persistence.jpa.repositories.ServiceOpeningIntervalJpaRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class ServiceOpeningIntervalRepositoryAdapter implements ServiceOpeningIntervalRepository {
  private final ServiceOpeningIntervalJpaRepository jpa;

  public ServiceOpeningIntervalRepositoryAdapter(ServiceOpeningIntervalJpaRepository jpa) {
    this.jpa = jpa;
  }

  public Optional<ServiceOpeningInterval> findById(UUID id) {
    return jpa.findById(id).map(ServiceOpeningIntervalPersistenceMapper::toDomain);
  }

  public java.util.List<ServiceOpeningInterval> findAll() {
    return jpa.findAll().stream().map(ServiceOpeningIntervalPersistenceMapper::toDomain).toList();
  }

  public ServiceOpeningInterval save(ServiceOpeningInterval a) {
    var e = jpa.findById(a.id()).orElseGet(ServiceOpeningIntervalJpaEntity::new);
    if (e.id != null && e.version != a.version())
      throw new org.springframework.orm.ObjectOptimisticLockingFailureException(
          ServiceOpeningInterval.class, a.id());
    ServiceOpeningIntervalPersistenceMapper.copy(a, e);
    return ServiceOpeningIntervalPersistenceMapper.toDomain(jpa.saveAndFlush(e));
  }

  public void delete(UUID id) {
    jpa.deleteById(id);
  }

  public java.util.List<ServiceOpeningInterval> findByServiceId(UUID value) {
    return jpa.findByServiceId(value).stream()
        .map(ServiceOpeningIntervalPersistenceMapper::toDomain)
        .toList();
  }
}
