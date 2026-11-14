package com.noctiva.noxway.community.infrastructure.persistence.jpa.repositories;

import com.noctiva.noxway.community.infrastructure.persistence.jpa.entities.CommunityPostPersistenceEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunityPostPersistenceRepository
    extends JpaRepository<CommunityPostPersistenceEntity, UUID> {
  List<CommunityPostPersistenceEntity> findByAuthorId(UUID value);
}
