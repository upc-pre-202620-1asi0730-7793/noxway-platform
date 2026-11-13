package com.noctiva.noxway.community.infrastructure.persistence.jpa.adapters;

import com.noctiva.noxway.community.domain.model.aggregates.ServiceVote;
import com.noctiva.noxway.community.domain.repositories.ServiceVoteRepository;
import com.noctiva.noxway.community.infrastructure.persistence.jpa.assemblers.CommunityPersistenceAssembler;
import com.noctiva.noxway.community.infrastructure.persistence.jpa.repositories.ServiceVotePersistenceRepository;
import com.noctiva.noxway.shared.infrastructure.events.DomainEventPublisher;
import com.noctiva.noxway.shared.infrastructure.persistence.jpa.adapters.OptimisticJpaSaveSupport;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class ServiceVoteRepositoryImpl implements ServiceVoteRepository {

  private final ServiceVotePersistenceRepository jpa;
  private final DomainEventPublisher events;

  public ServiceVoteRepositoryImpl(
      ServiceVotePersistenceRepository jpa, DomainEventPublisher events) {
    this.jpa = jpa;
    this.events = events;
  }

  @Override
  public Optional<ServiceVote> findById(UUID id) {
    return jpa.findById(id).map(CommunityPersistenceAssembler::toDomain);
  }

  @Override
  public List<ServiceVote> findAll() {
    return jpa.findAll().stream().map(CommunityPersistenceAssembler::toDomain).toList();
  }

  @Override
  public ServiceVote save(ServiceVote aggregate) {
    return OptimisticJpaSaveSupport.save(
        jpa,
        aggregate.getId(),
        aggregate.getVersion(),
        aggregate,
        CommunityPersistenceAssembler::toPersistence,
        CommunityPersistenceAssembler::toDomain,
        events,
        ServiceVote.class);
  }

  @Override
  public void delete(UUID id) {
    jpa.deleteById(id);
  }

  @Override
  public List<ServiceVote> findByServiceId(UUID value) {
    return jpa.findByServiceId(value).stream()
        .map(CommunityPersistenceAssembler::toDomain)
        .toList();
  }

  @Override
  public List<ServiceVote> findByAccountId(UUID value) {
    return jpa.findByAccountId(value).stream()
        .map(CommunityPersistenceAssembler::toDomain)
        .toList();
  }
}
