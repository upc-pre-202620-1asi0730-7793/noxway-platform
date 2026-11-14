package com.noctiva.noxway.iam.infrastructure.persistence.jpa.adapters;

import com.noctiva.noxway.iam.domain.model.aggregates.PasswordReset;
import com.noctiva.noxway.iam.domain.repositories.PasswordResetRepository;
import com.noctiva.noxway.iam.infrastructure.persistence.jpa.assemblers.PasswordResetPersistenceAssembler;
import com.noctiva.noxway.iam.infrastructure.persistence.jpa.repositories.PasswordResetPersistenceRepository;
import com.noctiva.noxway.shared.infrastructure.events.DomainEventPublisher;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import com.noctiva.noxway.shared.infrastructure.persistence.jpa.adapters.OptimisticJpaSaveSupport;
import org.springframework.stereotype.Repository;

@Repository
public class PasswordResetRepositoryImpl implements PasswordResetRepository {

  private final PasswordResetPersistenceRepository repository;
  private final DomainEventPublisher eventPublisher;

  public PasswordResetRepositoryImpl(
      PasswordResetPersistenceRepository repository, DomainEventPublisher eventPublisher) {
    this.repository = repository;
    this.eventPublisher = eventPublisher;
  }

  @Override
  public Optional<PasswordReset> findById(UUID id) {
    return repository.findById(id).map(PasswordResetPersistenceAssembler::toDomain);
  }

  @Override
  public List<PasswordReset> findAll() {
    return repository.findAll().stream().map(PasswordResetPersistenceAssembler::toDomain).toList();
  }

  @Override
  public PasswordReset save(PasswordReset entity) {
    return OptimisticJpaSaveSupport.save(
        repository,
        entity.getId(),
        entity.getVersion(),
        entity,
        PasswordResetPersistenceAssembler::toPersistence,
        PasswordResetPersistenceAssembler::toDomain,
        eventPublisher,
        PasswordReset.class);
  }

  @Override
  public void delete(UUID id) {
    repository.deleteById(id);
  }

  @Override
  public Optional<PasswordReset> findByTokenHash(String tokenHash) {
    return repository.findByTokenHash(tokenHash).map(PasswordResetPersistenceAssembler::toDomain);
  }

  @Override
  public List<PasswordReset> findByAccountId(UUID accountId) {
    return repository.findByAccountId(accountId).stream()
        .map(PasswordResetPersistenceAssembler::toDomain)
        .toList();
  }
}
