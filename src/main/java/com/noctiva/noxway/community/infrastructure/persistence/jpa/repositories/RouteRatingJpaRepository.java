package com.noctiva.noxway.community.infrastructure.persistence.jpa.repositories;

import com.noctiva.noxway.community.infrastructure.persistence.jpa.entities.RouteRatingJpaEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RouteRatingJpaRepository extends JpaRepository<RouteRatingJpaEntity, UUID> {
  java.util.List<RouteRatingJpaEntity> findByCommuteId(UUID value);

  java.util.List<RouteRatingJpaEntity> findByWorkerId(UUID value);
}
