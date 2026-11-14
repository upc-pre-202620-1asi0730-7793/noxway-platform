package com.noctiva.noxway.community.infrastructure.persistence.jpa.adapters;

import com.noctiva.noxway.community.domain.model.aggregates.RouteRating;
import com.noctiva.noxway.community.domain.repositories.RouteRatingRepository;
import com.noctiva.noxway.community.infrastructure.persistence.jpa.assemblers.CommunityPersistenceAssembler;
import com.noctiva.noxway.community.infrastructure.persistence.jpa.repositories.RouteRatingPersistenceRepository;
import com.noctiva.noxway.shared.infrastructure.events.DomainEventPublisher;
import com.noctiva.noxway.shared.infrastructure.persistence.jpa.adapters.OptimisticJpaSaveSupport;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class RouteRatingRepositoryImpl implements RouteRatingRepository {

  private final RouteRatingPersistenceRepository jpa;
  private final DomainEventPublisher events;

  public RouteRatingRepositoryImpl(
      RouteRatingPersistenceRepository jpa, DomainEventPublisher events) {
    this.jpa = jpa;
    this.events = events;
  }

  @Override
  public Optional<RouteRating> findById(UUID id) {
    return jpa.findById(id).map(CommunityPersistenceAssembler::toDomain);
  }

  @Override
  public List<RouteRating> findAll() {
    return jpa.findAll().stream().map(CommunityPersistenceAssembler::toDomain).toList();
  }

  @Override
  public RouteRating save(RouteRating aggregate) {
    return OptimisticJpaSaveSupport.save(
        jpa,
        aggregate.getId(),
        aggregate.getVersion(),
        aggregate,
        CommunityPersistenceAssembler::toPersistence,
        CommunityPersistenceAssembler::toDomain,
        events,
        RouteRating.class);
  }

  @Override
  public void delete(UUID id) {
    jpa.deleteById(id);
  }

  @Override
  public List<RouteRating> findByCommuteId(UUID value) {
    return jpa.findByCommuteId(value).stream()
        .map(CommunityPersistenceAssembler::toDomain)
        .toList();
  }

  @Override
  public List<RouteRating> findByWorkerId(UUID value) {
    return jpa.findByWorkerId(value).stream()
        .map(CommunityPersistenceAssembler::toDomain)
        .toList();
  }
}
