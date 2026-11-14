package com.noctiva.noxway.iam.infrastructure.persistence.jpa.adapters;

import com.noctiva.noxway.iam.domain.model.aggregates.AuthSession;
import com.noctiva.noxway.iam.domain.repositories.AuthSessionRepository;
import com.noctiva.noxway.iam.infrastructure.persistence.jpa.assemblers.AuthSessionPersistenceAssembler;
import com.noctiva.noxway.iam.infrastructure.persistence.jpa.repositories.AuthSessionPersistenceRepository;
import com.noctiva.noxway.shared.infrastructure.events.DomainEventPublisher;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import com.noctiva.noxway.shared.infrastructure.persistence.jpa.adapters.OptimisticJpaSaveSupport;
import org.springframework.stereotype.Repository;

@Repository
public class AuthSessionRepositoryImpl implements AuthSessionRepository {

  private final AuthSessionPersistenceRepository repository;
  private final DomainEventPublisher eventPublisher;

  public AuthSessionRepositoryImpl(
      AuthSessionPersistenceRepository repository, DomainEventPublisher eventPublisher) {
    this.repository = repository;
    this.eventPublisher = eventPublisher;
  }

  @Override
  public Optional<AuthSession> findById(UUID id) {
    return repository.findById(id).map(AuthSessionPersistenceAssembler::toDomain);
  }

  @Override
  public List<AuthSession> findAll() {
    return repository.findAll().stream().map(AuthSessionPersistenceAssembler::toDomain).toList();
  }

  @Override
  public AuthSession save(AuthSession entity) {
    return OptimisticJpaSaveSupport.save(
        repository,
        entity.getId(),
        entity.getVersion(),
        entity,
        AuthSessionPersistenceAssembler::toPersistence,
        AuthSessionPersistenceAssembler::toDomain,
        eventPublisher,
        AuthSession.class);
  }

  @Override
  public void delete(UUID id) {
    repository.deleteById(id);
  }

  @Override
  public Optional<AuthSession> findByTokenHash(String tokenHash) {
    return repository.findByTokenHash(tokenHash).map(AuthSessionPersistenceAssembler::toDomain);
  }

  @Override
  public List<AuthSession> findByAccountId(UUID accountId) {
    return repository.findByAccountId(accountId).stream()
        .map(AuthSessionPersistenceAssembler::toDomain)
        .toList();
  }
}
