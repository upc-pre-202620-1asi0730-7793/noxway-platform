package com.noctiva.noxway.commuting.infrastructure.persistence.jpa.adapters;

import com.noctiva.noxway.commuting.domain.model.aggregates.Alert;
import com.noctiva.noxway.commuting.domain.repositories.AlertRepository;
import com.noctiva.noxway.commuting.infrastructure.persistence.jpa.assemblers.CommutingPersistenceAssembler;
import com.noctiva.noxway.commuting.infrastructure.persistence.jpa.repositories.AlertPersistenceRepository;
import com.noctiva.noxway.shared.infrastructure.events.DomainEventPublisher;
import com.noctiva.noxway.shared.infrastructure.persistence.jpa.adapters.OptimisticJpaSaveSupport;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class AlertRepositoryImpl implements AlertRepository {

  private final AlertPersistenceRepository jpa;
  private final DomainEventPublisher events;

  public AlertRepositoryImpl(AlertPersistenceRepository jpa, DomainEventPublisher events) {
    this.jpa = jpa;
    this.events = events;
  }

  @Override
  public Optional<Alert> findById(UUID id) {
    return jpa.findById(id).map(CommutingPersistenceAssembler::toDomain);
  }

  @Override
  public List<Alert> findAll() {
    return jpa.findAll().stream().map(CommutingPersistenceAssembler::toDomain).toList();
  }

  @Override
  public Alert save(Alert aggregate) {
    return OptimisticJpaSaveSupport.save(
        jpa,
        aggregate.getId(),
        aggregate.getVersion(),
        aggregate,
        CommutingPersistenceAssembler::toPersistence,
        CommutingPersistenceAssembler::toDomain,
        events,
        Alert.class);
  }

  @Override
  public void delete(UUID id) {
    jpa.deleteById(id);
  }

  @Override
  public List<Alert> findByCommuteId(UUID value) {
    return jpa.findByCommuteId(value).stream().map(CommutingPersistenceAssembler::toDomain).toList();
  }

  @Override
  public List<Alert> findByIncidentId(UUID value) {
    return jpa.findByIncidentId(value).stream()
        .map(CommutingPersistenceAssembler::toDomain)
        .toList();
  }

  @Override
  public List<Alert> findByContactId(UUID value) {
    return jpa.findByContactId(value).stream().map(CommutingPersistenceAssembler::toDomain).toList();
  }

  @Override
  public List<Alert> findByStatus(String value) {
    return jpa.findByStatus(value).stream().map(CommutingPersistenceAssembler::toDomain).toList();
  }
}
