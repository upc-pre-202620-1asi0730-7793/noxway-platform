package com.noctiva.noxway.community.infrastructure.persistence.jpa.adapters;

import com.noctiva.noxway.community.domain.model.aggregates.CommunityPost;
import com.noctiva.noxway.community.domain.repositories.CommunityPostRepository;
import com.noctiva.noxway.community.infrastructure.persistence.jpa.assemblers.CommunityPersistenceAssembler;
import com.noctiva.noxway.community.infrastructure.persistence.jpa.repositories.CommunityPostPersistenceRepository;
import com.noctiva.noxway.shared.infrastructure.events.DomainEventPublisher;
import com.noctiva.noxway.shared.infrastructure.persistence.jpa.adapters.OptimisticJpaSaveSupport;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class CommunityPostRepositoryImpl implements CommunityPostRepository {

  private final CommunityPostPersistenceRepository jpa;
  private final DomainEventPublisher events;

  public CommunityPostRepositoryImpl(
      CommunityPostPersistenceRepository jpa, DomainEventPublisher events) {
    this.jpa = jpa;
    this.events = events;
  }

  @Override
  public Optional<CommunityPost> findById(UUID id) {
    return jpa.findById(id).map(CommunityPersistenceAssembler::toDomain);
  }

  @Override
  public List<CommunityPost> findAll() {
    return jpa.findAll().stream().map(CommunityPersistenceAssembler::toDomain).toList();
  }

  @Override
  public CommunityPost save(CommunityPost aggregate) {
    return OptimisticJpaSaveSupport.save(
        jpa,
        aggregate.getId(),
        aggregate.getVersion(),
        aggregate,
        CommunityPersistenceAssembler::toPersistence,
        CommunityPersistenceAssembler::toDomain,
        events,
        CommunityPost.class);
  }

  @Override
  public void delete(UUID id) {
    jpa.deleteById(id);
  }

  @Override
  public List<CommunityPost> findByAuthorId(UUID value) {
    return jpa.findByAuthorId(value).stream()
        .map(CommunityPersistenceAssembler::toDomain)
        .toList();
  }
}
