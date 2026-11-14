package com.noctiva.noxway.shared.infrastructure.persistence.jpa.adapters;

import com.noctiva.noxway.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import com.noctiva.noxway.shared.infrastructure.events.DomainEventPublisher;
import com.noctiva.noxway.shared.infrastructure.persistence.jpa.entities.AuditableUuidPersistenceEntity;
import java.util.UUID;
import java.util.function.Function;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

/**
 * Shared persistence save helper enforcing optimistic version checks and publishing domain events.
 */
public final class OptimisticJpaSaveSupport {
  private OptimisticJpaSaveSupport() {}

  public static <D extends AbstractDomainAggregateRoot<D>, E extends AuditableUuidPersistenceEntity>
      D save(
          JpaRepository<E, UUID> jpaRepository,
          UUID id,
          long expectedVersion,
          D aggregate,
          Function<D, E> toPersistence,
          Function<E, D> toDomain,
          DomainEventPublisher eventPublisher,
          Class<D> aggregateClass) {
    var existing = jpaRepository.findById(id);
    E entity = toPersistence.apply(aggregate);
    if (existing.isPresent()) {
      if (existing.get().getVersion() != expectedVersion) {
        throw new ObjectOptimisticLockingFailureException(aggregateClass, id);
      }
      entity.markExisting();
    }
    var saved = jpaRepository.saveAndFlush(entity);
    D domain = toDomain.apply(saved);
    eventPublisher.publishAndClearEvents(aggregate);
    return domain;
  }
}
