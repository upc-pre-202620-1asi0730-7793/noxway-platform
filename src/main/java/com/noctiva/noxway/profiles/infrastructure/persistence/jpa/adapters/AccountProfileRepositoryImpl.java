package com.noctiva.noxway.profiles.infrastructure.persistence.jpa.adapters;

import com.noctiva.noxway.profiles.domain.model.aggregates.AccountProfile;
import com.noctiva.noxway.profiles.domain.repositories.AccountProfileRepository;
import com.noctiva.noxway.profiles.infrastructure.persistence.jpa.assemblers.AccountPersistenceAssembler;
import com.noctiva.noxway.profiles.infrastructure.persistence.jpa.repositories.AccountPersistenceRepository;
import com.noctiva.noxway.shared.infrastructure.events.DomainEventPublisher;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import com.noctiva.noxway.shared.infrastructure.persistence.jpa.adapters.OptimisticJpaSaveSupport;
import org.springframework.stereotype.Repository;

@Repository
public class AccountProfileRepositoryImpl implements AccountProfileRepository {

  private final AccountPersistenceRepository repository;
  private final DomainEventPublisher eventPublisher;

  public AccountProfileRepositoryImpl(
      AccountPersistenceRepository repository, DomainEventPublisher eventPublisher) {
    this.repository = repository;
    this.eventPublisher = eventPublisher;
  }

  @Override
  public Optional<AccountProfile> findById(UUID id) {
    return repository.findById(id).map(AccountPersistenceAssembler::toDomain);
  }

  @Override
  public List<AccountProfile> findAll() {
    return repository.findAll().stream().map(AccountPersistenceAssembler::toDomain).toList();
  }

  @Override
  public AccountProfile save(AccountProfile entity) {
    return OptimisticJpaSaveSupport.save(
        repository,
        entity.getId(),
        entity.getVersion(),
        entity,
        AccountPersistenceAssembler::toPersistence,
        AccountPersistenceAssembler::toDomain,
        eventPublisher,
        AccountProfile.class);
  }

  @Override
  public void delete(UUID id) {
    repository.deleteById(id);
  }

  @Override
  public Optional<AccountProfile> findByEmail(String email) {
    return repository.findByEmail(email).map(AccountPersistenceAssembler::toDomain);
  }

  @Override
  public boolean existsByEmail(String email) {
    return repository.existsByEmail(email);
  }

  @Override
  public List<AccountProfile> findByActiveTrueOrderByCreatedAtAsc() {
    return repository.findByActiveTrueOrderByCreatedAtAsc().stream()
        .map(AccountPersistenceAssembler::toDomain)
        .toList();
  }
}
