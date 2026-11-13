package com.noctiva.noxway.moderation.infrastructure.persistence.jpa.adapters;

import com.noctiva.noxway.moderation.domain.model.aggregates.ModerationAction;
import com.noctiva.noxway.moderation.domain.repositories.ModerationActionRepository;
import com.noctiva.noxway.moderation.infrastructure.persistence.jpa.assemblers.ModerationPersistenceAssembler;
import com.noctiva.noxway.moderation.infrastructure.persistence.jpa.repositories.ModerationActionPersistenceRepository;
import com.noctiva.noxway.shared.infrastructure.events.DomainEventPublisher;
import com.noctiva.noxway.shared.infrastructure.persistence.jpa.adapters.OptimisticJpaSaveSupport;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class ModerationActionRepositoryImpl implements ModerationActionRepository {

  private final ModerationActionPersistenceRepository jpa;
  private final DomainEventPublisher events;

  public ModerationActionRepositoryImpl(
      ModerationActionPersistenceRepository jpa, DomainEventPublisher events) {
    this.jpa = jpa;
    this.events = events;
  }

  @Override
  public Optional<ModerationAction> findById(UUID id) {
    return jpa.findById(id).map(ModerationPersistenceAssembler::toDomain);
  }

  @Override
  public List<ModerationAction> findAll() {
    return jpa.findAll().stream().map(ModerationPersistenceAssembler::toDomain).toList();
  }

  @Override
  public ModerationAction save(ModerationAction aggregate) {
    return OptimisticJpaSaveSupport.save(
        jpa,
        aggregate.getId(),
        aggregate.getVersion(),
        aggregate,
        ModerationPersistenceAssembler::toPersistence,
        ModerationPersistenceAssembler::toDomain,
        events,
        ModerationAction.class);
  }

  @Override
  public void delete(UUID id) {
    jpa.deleteById(id);
  }

  @Override
  public List<ModerationAction> findByReportId(UUID value) {
    return jpa.findByReportId(value).stream()
        .map(ModerationPersistenceAssembler::toDomain)
        .toList();
  }

  @Override
  public List<ModerationAction> findByModeratorId(UUID value) {
    return jpa.findByModeratorId(value).stream()
        .map(ModerationPersistenceAssembler::toDomain)
        .toList();
  }
}
