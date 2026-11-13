package com.noctiva.noxway.wellness.infrastructure.persistence.jpa.adapters;

import com.noctiva.noxway.shared.infrastructure.events.DomainEventPublisher;
import com.noctiva.noxway.wellness.domain.model.aggregates.SleepGoal;
import com.noctiva.noxway.wellness.domain.repositories.SleepGoalRepository;
import com.noctiva.noxway.wellness.infrastructure.persistence.jpa.assemblers.SleepGoalPersistenceAssembler;
import com.noctiva.noxway.wellness.infrastructure.persistence.jpa.repositories.SleepGoalPersistenceRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import com.noctiva.noxway.shared.infrastructure.persistence.jpa.adapters.OptimisticJpaSaveSupport;
import org.springframework.stereotype.Repository;

@Repository
public class SleepGoalRepositoryImpl implements SleepGoalRepository {

  private final SleepGoalPersistenceRepository repository;
  private final DomainEventPublisher eventPublisher;

  public SleepGoalRepositoryImpl(
      SleepGoalPersistenceRepository repository, DomainEventPublisher eventPublisher) {
    this.repository = repository;
    this.eventPublisher = eventPublisher;
  }

  @Override
  public Optional<SleepGoal> findById(UUID id) {
    return repository.findById(id).map(SleepGoalPersistenceAssembler::toDomain);
  }

  @Override
  public List<SleepGoal> findAll() {
    return repository.findAll().stream().map(SleepGoalPersistenceAssembler::toDomain).toList();
  }

  @Override
  public SleepGoal save(SleepGoal entity) {
    return OptimisticJpaSaveSupport.save(
        repository,
        entity.getId(),
        entity.getVersion(),
        entity,
        SleepGoalPersistenceAssembler::toPersistence,
        SleepGoalPersistenceAssembler::toDomain,
        eventPublisher,
        SleepGoal.class);
  }

  @Override
  public void delete(UUID id) {
    repository.deleteById(id);
  }

  @Override
  public List<SleepGoal> findByWorkerId(UUID value) {
    return repository.findByWorkerId(value).stream()
        .map(SleepGoalPersistenceAssembler::toDomain)
        .toList();
  }
}
