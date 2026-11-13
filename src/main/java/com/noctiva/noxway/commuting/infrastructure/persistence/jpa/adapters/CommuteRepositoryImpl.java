package com.noctiva.noxway.commuting.infrastructure.persistence.jpa.adapters;

import com.noctiva.noxway.commuting.domain.model.aggregates.Commute;
import com.noctiva.noxway.commuting.domain.repositories.CommuteRepository;
import com.noctiva.noxway.commuting.infrastructure.persistence.jpa.assemblers.CommutingPersistenceAssembler;
import com.noctiva.noxway.commuting.infrastructure.persistence.jpa.repositories.CommutePersistenceRepository;
import com.noctiva.noxway.shared.infrastructure.events.DomainEventPublisher;
import com.noctiva.noxway.shared.infrastructure.persistence.jpa.adapters.OptimisticJpaSaveSupport;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class CommuteRepositoryImpl implements CommuteRepository {

  private final CommutePersistenceRepository jpa;
  private final DomainEventPublisher events;

  public CommuteRepositoryImpl(CommutePersistenceRepository jpa, DomainEventPublisher events) {
    this.jpa = jpa;
    this.events = events;
  }

  @Override
  public Optional<Commute> findById(UUID id) {
    return jpa.findById(id).map(CommutingPersistenceAssembler::toDomain);
  }

  @Override
  public List<Commute> findAll() {
    return jpa.findAll().stream().map(CommutingPersistenceAssembler::toDomain).toList();
  }

  @Override
  public Commute save(Commute aggregate) {
    return OptimisticJpaSaveSupport.save(
        jpa,
        aggregate.getId(),
        aggregate.getVersion(),
        aggregate,
        CommutingPersistenceAssembler::toPersistence,
        CommutingPersistenceAssembler::toDomain,
        events,
        Commute.class);
  }

  @Override
  public void delete(UUID id) {
    jpa.deleteById(id);
  }

  @Override
  public List<Commute> findByWorkerId(UUID value) {
    return jpa.findByWorkerId(value).stream().map(CommutingPersistenceAssembler::toDomain).toList();
  }

  @Override
  public List<Commute> findByStatus(String value) {
    return jpa.findByStatus(value).stream().map(CommutingPersistenceAssembler::toDomain).toList();
  }
}
