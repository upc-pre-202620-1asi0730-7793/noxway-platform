package com.noctiva.noxway.community.infrastructure.persistence.jpa.mappers;

import com.noctiva.noxway.community.domain.model.aggregates.RouteRating;
import com.noctiva.noxway.community.infrastructure.persistence.jpa.entities.RouteRatingJpaEntity;

public final class RouteRatingPersistenceMapper {
  private RouteRatingPersistenceMapper() {}

  public static RouteRating toDomain(RouteRatingJpaEntity e) {
    return new RouteRating(
        e.id, e.commuteId, e.workerId, e.score, e.comments, e.createdAt, e.version);
  }

  public static void copy(RouteRating a, RouteRatingJpaEntity e) {
    e.id = a.id();
    e.commuteId = a.commuteId();
    e.workerId = a.workerId();
    e.score = a.score();
    e.comments = a.comments();
    e.createdAt = a.createdAt();
  }
}
