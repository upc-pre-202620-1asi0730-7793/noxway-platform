package com.noctiva.noxway;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.noctiva.noxway.commuting.domain.model.aggregates.Commute;
import com.noctiva.noxway.commuting.infrastructure.persistence.jpa.adapters.CommuteRepositoryImpl;
import com.noctiva.noxway.commuting.infrastructure.persistence.jpa.entities.CommutePersistenceEntity;
import com.noctiva.noxway.commuting.infrastructure.persistence.jpa.repositories.CommutePersistenceRepository;
import com.noctiva.noxway.profiles.domain.model.aggregates.AccountProfile;
import com.noctiva.noxway.profiles.infrastructure.persistence.jpa.adapters.AccountProfileRepositoryImpl;
import com.noctiva.noxway.profiles.infrastructure.persistence.jpa.entities.AccountPersistenceEntity;
import com.noctiva.noxway.profiles.infrastructure.persistence.jpa.repositories.AccountPersistenceRepository;
import com.noctiva.noxway.shared.infrastructure.events.DomainEventPublisher;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

class RepositoryAdaptersTest {

  @Test
  void accountProfileAdapterMarksExistingZeroVersionEntityAndPublishesEvents() {
    AccountPersistenceRepository jpa = mock(AccountPersistenceRepository.class);
    DomainEventPublisher publisher = mock(DomainEventPublisher.class);
    AccountProfileRepositoryImpl adapter = new AccountProfileRepositoryImpl(jpa, publisher);

    UUID id = UUID.randomUUID();
    AccountProfile aggregate =
        AccountProfile.reconstitute(
            id,
            "worker@noxway.pe",
            "Jorge Huam\u00e1n",
            "+51987654321",
            "WORKER",
            "NIGHT",
            "22:00",
            "06:00",
            "America/Lima",
            "hash",
            true,
            Instant.now(),
            0L);

    AccountPersistenceEntity existing = new AccountPersistenceEntity();
    existing.setId(id);
    existing.setVersion(0L);
    when(jpa.findById(id)).thenReturn(Optional.of(existing));
    when(jpa.saveAndFlush(any(AccountPersistenceEntity.class)))
        .thenAnswer(
            inv -> {
              AccountPersistenceEntity p = inv.getArgument(0);
              assertFalse(p.isNew(), "Existing entity with version=0 must be marked non-new so JPA merges");
              return p;
            });

    AccountProfile saved = adapter.save(aggregate);
    assertEquals(id, saved.getId());
    verify(publisher).publishAndClearEvents(aggregate);
  }

  @Test
  void accountProfileAdapterThrowsOptimisticLockWhenVersionMismatches() {
    AccountPersistenceRepository jpa = mock(AccountPersistenceRepository.class);
    DomainEventPublisher publisher = mock(DomainEventPublisher.class);
    AccountProfileRepositoryImpl adapter = new AccountProfileRepositoryImpl(jpa, publisher);

    UUID id = UUID.randomUUID();
    AccountProfile staleAggregate =
        AccountProfile.reconstitute(
            id,
            "worker@noxway.pe",
            "Jorge Huam\u00e1n",
            "+51987654321",
            "WORKER",
            "NIGHT",
            "22:00",
            "06:00",
            "America/Lima",
            "hash",
            true,
            Instant.now(),
            0L);

    AccountPersistenceEntity newerInDb = new AccountPersistenceEntity();
    newerInDb.setId(id);
    newerInDb.setVersion(3L);
    when(jpa.findById(id)).thenReturn(Optional.of(newerInDb));

    assertThrows(ObjectOptimisticLockingFailureException.class, () -> adapter.save(staleAggregate));
    verify(jpa, never()).saveAndFlush(any());
    verify(publisher, never()).publishAndClearEvents(any());
  }

  @Test
  void commuteAdapterMarksExistingEntityAndPublishesEventsOnArrival() {
    CommutePersistenceRepository jpa = mock(CommutePersistenceRepository.class);
    DomainEventPublisher publisher = mock(DomainEventPublisher.class);
    CommuteRepositoryImpl adapter = new CommuteRepositoryImpl(jpa, publisher);

    Instant start = Instant.now().minusSeconds(300);
    Commute commute =
        Commute.start(
            UUID.randomUUID(),
            "Hospital",
            "Home",
            "BUS",
            start,
            start.plusSeconds(1800),
            10);
    commute.arrive(Instant.now());

    CommutePersistenceEntity existing = new CommutePersistenceEntity();
    existing.setId(commute.getId());
    existing.setVersion(0L);
    when(jpa.findById(commute.getId())).thenReturn(Optional.of(existing));
    when(jpa.saveAndFlush(any(CommutePersistenceEntity.class)))
        .thenAnswer(
            inv -> {
              CommutePersistenceEntity p = inv.getArgument(0);
              assertFalse(p.isNew());
              return p;
            });

    Commute saved = adapter.save(commute);
    assertEquals("ARRIVED", saved.getStatus());
    verify(publisher).publishAndClearEvents(commute);
  }
}
