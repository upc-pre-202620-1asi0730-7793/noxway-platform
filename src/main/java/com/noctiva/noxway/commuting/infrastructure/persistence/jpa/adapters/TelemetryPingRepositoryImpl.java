package com.noctiva.noxway.commuting.infrastructure.persistence.jpa.adapters;

import com.noctiva.noxway.commuting.domain.model.aggregates.TelemetryPing;
import com.noctiva.noxway.commuting.domain.repositories.TelemetryPingRepository;
import com.noctiva.noxway.commuting.infrastructure.persistence.jpa.assemblers.CommutingPersistenceAssembler;
import com.noctiva.noxway.commuting.infrastructure.persistence.jpa.repositories.TelemetryPingPersistenceRepository;
import com.noctiva.noxway.shared.infrastructure.events.DomainEventPublisher;
import com.noctiva.noxway.shared.infrastructure.persistence.jpa.adapters.OptimisticJpaSaveSupport;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class TelemetryPingRepositoryImpl implements TelemetryPingRepository {

  private final TelemetryPingPersistenceRepository jpa;
  private final DomainEventPublisher events;

  public TelemetryPingRepositoryImpl(
      TelemetryPingPersistenceRepository jpa, DomainEventPublisher events) {
    this.jpa = jpa;
    this.events = events;
  }

  @Override
  public Optional<TelemetryPing> findById(UUID id) {
    return jpa.findById(id).map(CommutingPersistenceAssembler::toDomain);
  }

  @Override
  public List<TelemetryPing> findAll() {
    return jpa.findAll().stream().map(CommutingPersistenceAssembler::toDomain).toList();
  }

  @Override
  public TelemetryPing save(TelemetryPing aggregate) {
    return OptimisticJpaSaveSupport.save(
        jpa,
        aggregate.getId(),
        aggregate.getVersion(),
        aggregate,
        CommutingPersistenceAssembler::toPersistence,
        CommutingPersistenceAssembler::toDomain,
        events,
        TelemetryPing.class);
  }

  @Override
  public void delete(UUID id) {
    jpa.deleteById(id);
  }

  @Override
  public List<TelemetryPing> findByCommuteId(UUID value) {
    return jpa.findByCommuteId(value).stream()
        .map(CommutingPersistenceAssembler::toDomain)
        .toList();
  }
}
