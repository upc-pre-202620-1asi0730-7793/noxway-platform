package com.noctiva.noxway.profiles.infrastructure.persistence.jpa.adapters;

import com.noctiva.noxway.profiles.domain.model.aggregates.TrustLink;
import com.noctiva.noxway.profiles.domain.repositories.TrustLinkRepository;
import com.noctiva.noxway.profiles.infrastructure.persistence.jpa.assemblers.TrustLinkPersistenceAssembler;
import com.noctiva.noxway.profiles.infrastructure.persistence.jpa.repositories.TrustLinkPersistenceRepository;
import com.noctiva.noxway.shared.infrastructure.events.DomainEventPublisher;
import com.noctiva.noxway.shared.infrastructure.persistence.jpa.adapters.OptimisticJpaSaveSupport;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class TrustLinkRepositoryImpl implements TrustLinkRepository {

  private final TrustLinkPersistenceRepository repository;
  private final DomainEventPublisher eventPublisher;

  public TrustLinkRepositoryImpl(
      TrustLinkPersistenceRepository repository, DomainEventPublisher eventPublisher) {
    this.repository = repository;
    this.eventPublisher = eventPublisher;
  }

  @Override
  public Optional<TrustLink> findById(UUID id) {
    return repository.findById(id).map(TrustLinkPersistenceAssembler::toDomain);
  }

  @Override
  public List<TrustLink> findAll() {
    return repository.findAll().stream().map(TrustLinkPersistenceAssembler::toDomain).toList();
  }

  @Override
  public TrustLink save(TrustLink entity) {
    return OptimisticJpaSaveSupport.save(
        repository,
        entity.getId(),
        entity.getVersion(),
        entity,
        TrustLinkPersistenceAssembler::toPersistence,
        TrustLinkPersistenceAssembler::toDomain,
        eventPublisher,
        TrustLink.class);
  }

  @Override
  public void delete(UUID id) {
    repository.deleteById(id);
    repository.flush();
  }

  @Override
  public List<TrustLink> findByWorkerId(UUID workerId) {
    return repository.findByWorkerId(workerId).stream()
        .map(TrustLinkPersistenceAssembler::toDomain)
        .toList();
  }

  @Override
  public List<TrustLink> findByContactId(UUID contactId) {
    return repository.findByContactId(contactId).stream()
        .map(TrustLinkPersistenceAssembler::toDomain)
        .toList();
  }

  @Override
  public Optional<TrustLink> findByWorkerIdAndContactId(UUID workerId, UUID contactId) {
    return repository
        .findByWorkerIdAndContactId(workerId, contactId)
        .map(TrustLinkPersistenceAssembler::toDomain);
  }

  @Override
  public boolean existsByWorkerIdAndContactIdAndStatus(UUID workerId, UUID contactId, String status) {
    return repository.existsByWorkerIdAndContactIdAndStatus(workerId, contactId, status);
  }
}
