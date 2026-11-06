package com.noctiva.noxway.community.infrastructure.persistence.jpa.adapters;

import com.noctiva.noxway.community.domain.model.aggregates.RouteRating;
import com.noctiva.noxway.community.domain.repositories.RouteRatingRepository;
import com.noctiva.noxway.community.infrastructure.persistence.jpa.entities.RouteRatingJpaEntity;
import com.noctiva.noxway.community.infrastructure.persistence.jpa.mappers.RouteRatingPersistenceMapper;
import com.noctiva.noxway.community.infrastructure.persistence.jpa.repositories.RouteRatingJpaRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class RouteRatingRepositoryAdapter implements RouteRatingRepository {
  private final RouteRatingJpaRepository jpa;

  public RouteRatingRepositoryAdapter(RouteRatingJpaRepository jpa) {
    this.jpa = jpa;
  }

  public Optional<RouteRating> findById(UUID id) {
    return jpa.findById(id).map(RouteRatingPersistenceMapper::toDomain);
  }

  public java.util.List<RouteRating> findAll() {
    return jpa.findAll().stream().map(RouteRatingPersistenceMapper::toDomain).toList();
  }

  public RouteRating save(RouteRating a) {
    var e = jpa.findById(a.id()).orElseGet(RouteRatingJpaEntity::new);
    if (e.id != null && e.version != a.version())
      throw new org.springframework.orm.ObjectOptimisticLockingFailureException(
          RouteRating.class, a.id());
    RouteRatingPersistenceMapper.copy(a, e);
    return RouteRatingPersistenceMapper.toDomain(jpa.saveAndFlush(e));
  }

  public void delete(UUID id) {
    jpa.deleteById(id);
  }

  public java.util.List<RouteRating> findByCommuteId(UUID value) {
    return jpa.findByCommuteId(value).stream().map(RouteRatingPersistenceMapper::toDomain).toList();
  }

  public java.util.List<RouteRating> findByWorkerId(UUID value) {
    return jpa.findByWorkerId(value).stream().map(RouteRatingPersistenceMapper::toDomain).toList();
  }
}
