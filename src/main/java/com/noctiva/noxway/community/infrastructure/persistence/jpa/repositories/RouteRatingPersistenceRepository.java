package com.noctiva.noxway.community.infrastructure.persistence.jpa.repositories;

import com.noctiva.noxway.community.infrastructure.persistence.jpa.entities.RouteRatingPersistenceEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RouteRatingPersistenceRepository
    extends JpaRepository<RouteRatingPersistenceEntity, UUID> {
  List<RouteRatingPersistenceEntity> findByCommuteId(UUID value);

  List<RouteRatingPersistenceEntity> findByWorkerId(UUID value);
}
