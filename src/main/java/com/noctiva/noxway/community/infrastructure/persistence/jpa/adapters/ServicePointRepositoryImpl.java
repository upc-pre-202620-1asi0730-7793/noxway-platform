package com.noctiva.noxway.community.infrastructure.persistence.jpa.adapters;

import com.noctiva.noxway.community.domain.model.aggregates.ServicePoint;
import com.noctiva.noxway.community.domain.repositories.ServicePointRepository;
import com.noctiva.noxway.community.infrastructure.persistence.jpa.assemblers.CommunityPersistenceAssembler;
import com.noctiva.noxway.community.infrastructure.persistence.jpa.repositories.ServicePointPersistenceRepository;
import com.noctiva.noxway.shared.infrastructure.events.DomainEventPublisher;
import com.noctiva.noxway.shared.infrastructure.persistence.jpa.adapters.OptimisticJpaSaveSupport;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class ServicePointRepositoryImpl implements ServicePointRepository {

  private final ServicePointPersistenceRepository jpa;
  private final DomainEventPublisher events;

  public ServicePointRepositoryImpl(
      ServicePointPersistenceRepository jpa, DomainEventPublisher events) {
    this.jpa = jpa;
    this.events = events;
  }

  @Override
  public Optional<ServicePoint> findById(UUID id) {
    return jpa.findById(id).map(CommunityPersistenceAssembler::toDomain);
  }

  @Override
  public List<ServicePoint> findAll() {
    return jpa.findAll().stream().map(CommunityPersistenceAssembler::toDomain).toList();
  }

  @Override
  public ServicePoint save(ServicePoint aggregate) {
    return OptimisticJpaSaveSupport.save(
        jpa,
        aggregate.getId(),
        aggregate.getVersion(),
        aggregate,
        CommunityPersistenceAssembler::toPersistence,
        CommunityPersistenceAssembler::toDomain,
        events,
        ServicePoint.class);
  }

  @Override
  public void delete(UUID id) {
    jpa.deleteById(id);
  }

  @Override
  public List<ServicePoint> findByReportedBy(UUID value) {
    return jpa.findByReportedBy(value).stream()
        .map(CommunityPersistenceAssembler::toDomain)
        .toList();
  }

  @Override
  public List<ServicePoint> findByStatus(String value) {
    return jpa.findByStatus(value).stream()
        .map(CommunityPersistenceAssembler::toDomain)
        .toList();
  }
}
