package com.noctiva.noxway.wellness.infrastructure.persistence.jpa.adapters;

import com.noctiva.noxway.shared.infrastructure.events.DomainEventPublisher;
import com.noctiva.noxway.wellness.domain.model.aggregates.RestSession;
import com.noctiva.noxway.wellness.domain.repositories.RestSessionRepository;
import com.noctiva.noxway.wellness.infrastructure.persistence.jpa.assemblers.RestSessionPersistenceAssembler;
import com.noctiva.noxway.wellness.infrastructure.persistence.jpa.repositories.RestSessionPersistenceRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import com.noctiva.noxway.shared.infrastructure.persistence.jpa.adapters.OptimisticJpaSaveSupport;
import org.springframework.stereotype.Repository;

@Repository
public class RestSessionRepositoryImpl implements RestSessionRepository {

  private final RestSessionPersistenceRepository repository;
  private final DomainEventPublisher eventPublisher;

  public RestSessionRepositoryImpl(
      RestSessionPersistenceRepository repository, DomainEventPublisher eventPublisher) {
    this.repository = repository;
    this.eventPublisher = eventPublisher;
  }

  @Override
  public Optional<RestSession> findById(UUID id) {
    return repository.findById(id).map(RestSessionPersistenceAssembler::toDomain);
  }

  @Override
  public List<RestSession> findAll() {
    return repository.findAll().stream().map(RestSessionPersistenceAssembler::toDomain).toList();
  }

  @Override
  public RestSession save(RestSession entity) {
    return OptimisticJpaSaveSupport.save(
        repository,
        entity.getId(),
        entity.getVersion(),
        entity,
        RestSessionPersistenceAssembler::toPersistence,
        RestSessionPersistenceAssembler::toDomain,
        eventPublisher,
        RestSession.class);
  }

  @Override
  public void delete(UUID id) {
    repository.deleteById(id);
  }

  @Override
  public List<RestSession> findByWorkerId(UUID value) {
    return repository.findByWorkerId(value).stream()
        .map(RestSessionPersistenceAssembler::toDomain)
        .toList();
  }
}
