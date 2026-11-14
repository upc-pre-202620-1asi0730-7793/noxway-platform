package com.noctiva.noxway.commuting.infrastructure.persistence.jpa.adapters;

import com.noctiva.noxway.commuting.domain.model.aggregates.SafetyIncident;
import com.noctiva.noxway.commuting.domain.repositories.SafetyIncidentRepository;
import com.noctiva.noxway.commuting.infrastructure.persistence.jpa.assemblers.CommutingPersistenceAssembler;
import com.noctiva.noxway.commuting.infrastructure.persistence.jpa.repositories.SafetyIncidentPersistenceRepository;
import com.noctiva.noxway.shared.infrastructure.events.DomainEventPublisher;
import com.noctiva.noxway.shared.infrastructure.persistence.jpa.adapters.OptimisticJpaSaveSupport;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class SafetyIncidentRepositoryImpl implements SafetyIncidentRepository {

  private final SafetyIncidentPersistenceRepository jpa;
  private final DomainEventPublisher events;

  public SafetyIncidentRepositoryImpl(
      SafetyIncidentPersistenceRepository jpa, DomainEventPublisher events) {
    this.jpa = jpa;
    this.events = events;
  }

  @Override
  public Optional<SafetyIncident> findById(UUID id) {
    return jpa.findById(id).map(CommutingPersistenceAssembler::toDomain);
  }

  @Override
  public List<SafetyIncident> findAll() {
    return jpa.findAll().stream().map(CommutingPersistenceAssembler::toDomain).toList();
  }

  @Override
  public SafetyIncident save(SafetyIncident aggregate) {
    return OptimisticJpaSaveSupport.save(
        jpa,
        aggregate.getId(),
        aggregate.getVersion(),
        aggregate,
        CommutingPersistenceAssembler::toPersistence,
        CommutingPersistenceAssembler::toDomain,
        events,
        SafetyIncident.class);
  }

  @Override
  public void delete(UUID id) {
    jpa.deleteById(id);
  }

  @Override
  public List<SafetyIncident> findByCommuteId(UUID value) {
    return jpa.findByCommuteId(value).stream()
        .map(CommutingPersistenceAssembler::toDomain)
        .toList();
  }

  @Override
  public List<SafetyIncident> findByWorkerId(UUID value) {
    return jpa.findByWorkerId(value).stream()
        .map(CommutingPersistenceAssembler::toDomain)
        .toList();
  }

  @Override
  public List<SafetyIncident> findByStatus(String value) {
    return jpa.findByStatus(value).stream().map(CommutingPersistenceAssembler::toDomain).toList();
  }
}
