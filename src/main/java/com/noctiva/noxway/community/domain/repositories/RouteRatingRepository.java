package com.noctiva.noxway.community.domain.repositories;

import com.noctiva.noxway.community.domain.model.aggregates.RouteRating;
import com.noctiva.noxway.shared.domain.DomainRepository;
import java.util.UUID;

public interface RouteRatingRepository extends DomainRepository<RouteRating> {
  java.util.List<RouteRating> findByCommuteId(UUID value);

  java.util.List<RouteRating> findByWorkerId(UUID value);
}
