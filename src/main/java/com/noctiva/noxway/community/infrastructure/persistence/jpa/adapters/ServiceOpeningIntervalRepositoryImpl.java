package com.noctiva.noxway.community.infrastructure.persistence.jpa.adapters;

import com.noctiva.noxway.community.domain.model.aggregates.ServiceOpeningInterval;
import com.noctiva.noxway.community.domain.repositories.ServiceOpeningIntervalRepository;
import com.noctiva.noxway.community.infrastructure.persistence.jpa.assemblers.CommunityPersistenceAssembler;
import com.noctiva.noxway.community.infrastructure.persistence.jpa.repositories.ServiceOpeningIntervalPersistenceRepository;
import com.noctiva.noxway.shared.infrastructure.events.DomainEventPublisher;
import com.noctiva.noxway.shared.infrastructure.persistence.jpa.adapters.OptimisticJpaSaveSupport;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class ServiceOpeningIntervalRepositoryImpl implements ServiceOpeningIntervalRepository {

  private final ServiceOpeningIntervalPersistenceRepository jpa;
  private final DomainEventPublisher events;

  public ServiceOpeningIntervalRepositoryImpl(
      ServiceOpeningIntervalPersistenceRepository jpa, DomainEventPublisher events) {
    this.jpa = jpa;
    this.events = events;
  }

  @Override
  public Optional<ServiceOpeningInterval> findById(UUID id) {
    return jpa.findById(id).map(CommunityPersistenceAssembler::toDomain);
  }

  @Override
  public List<ServiceOpeningInterval> findAll() {
    return jpa.findAll().stream().map(CommunityPersistenceAssembler::toDomain).toList();
  }

  @Override
  public ServiceOpeningInterval save(ServiceOpeningInterval aggregate) {
    return OptimisticJpaSaveSupport.save(
        jpa,
        aggregate.getId(),
        aggregate.getVersion(),
        aggregate,
        CommunityPersistenceAssembler::toPersistence,
        CommunityPersistenceAssembler::toDomain,
        events,
        ServiceOpeningInterval.class);
  }

  @Override
  public void delete(UUID id) {
    jpa.deleteById(id);
  }

  @Override
  public List<ServiceOpeningInterval> findByServiceId(UUID value) {
    return jpa.findByServiceId(value).stream()
        .map(CommunityPersistenceAssembler::toDomain)
        .toList();
  }
}
