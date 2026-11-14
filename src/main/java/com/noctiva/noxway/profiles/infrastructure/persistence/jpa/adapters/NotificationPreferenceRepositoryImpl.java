package com.noctiva.noxway.profiles.infrastructure.persistence.jpa.adapters;

import com.noctiva.noxway.profiles.domain.model.aggregates.NotificationPreference;
import com.noctiva.noxway.profiles.domain.repositories.NotificationPreferenceRepository;
import com.noctiva.noxway.profiles.infrastructure.persistence.jpa.assemblers.NotificationPreferencePersistenceAssembler;
import com.noctiva.noxway.profiles.infrastructure.persistence.jpa.repositories.NotificationPreferencePersistenceRepository;
import com.noctiva.noxway.shared.infrastructure.events.DomainEventPublisher;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import com.noctiva.noxway.shared.infrastructure.persistence.jpa.adapters.OptimisticJpaSaveSupport;
import org.springframework.stereotype.Repository;

@Repository
public class NotificationPreferenceRepositoryImpl implements NotificationPreferenceRepository {

  private final NotificationPreferencePersistenceRepository repository;
  private final DomainEventPublisher eventPublisher;

  public NotificationPreferenceRepositoryImpl(
      NotificationPreferencePersistenceRepository repository,
      DomainEventPublisher eventPublisher) {
    this.repository = repository;
    this.eventPublisher = eventPublisher;
  }

  @Override
  public Optional<NotificationPreference> findById(UUID id) {
    return repository.findById(id).map(NotificationPreferencePersistenceAssembler::toDomain);
  }

  @Override
  public List<NotificationPreference> findAll() {
    return repository.findAll().stream()
        .map(NotificationPreferencePersistenceAssembler::toDomain)
        .toList();
  }

  @Override
  public NotificationPreference save(NotificationPreference entity) {
    return OptimisticJpaSaveSupport.save(
        repository,
        entity.getId(),
        entity.getVersion(),
        entity,
        NotificationPreferencePersistenceAssembler::toPersistence,
        NotificationPreferencePersistenceAssembler::toDomain,
        eventPublisher,
        NotificationPreference.class);
  }

  @Override
  public void delete(UUID id) {
    repository.deleteById(id);
  }

  @Override
  public List<NotificationPreference> findByAccountId(UUID accountId) {
    return repository.findByAccountId(accountId).stream()
        .map(NotificationPreferencePersistenceAssembler::toDomain)
        .toList();
  }

  @Override
  public Optional<NotificationPreference> findByAccountIdAndChannel(UUID accountId, String channel) {
    return repository
        .findByAccountIdAndChannel(accountId, channel)
        .map(NotificationPreferencePersistenceAssembler::toDomain);
  }
}
