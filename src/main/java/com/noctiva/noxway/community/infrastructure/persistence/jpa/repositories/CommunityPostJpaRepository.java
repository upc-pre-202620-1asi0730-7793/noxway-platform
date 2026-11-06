package com.noctiva.noxway.community.infrastructure.persistence.jpa.repositories;

import com.noctiva.noxway.community.infrastructure.persistence.jpa.entities.CommunityPostJpaEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunityPostJpaRepository extends JpaRepository<CommunityPostJpaEntity, UUID> {
  java.util.List<CommunityPostJpaEntity> findByAuthorId(UUID value);
}
