package com.noctiva.noxway.community.infrastructure.persistence.jpa.repositories;

import com.noctiva.noxway.community.infrastructure.persistence.jpa.entities.ServiceVoteJpaEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceVoteJpaRepository extends JpaRepository<ServiceVoteJpaEntity, UUID> {
  java.util.List<ServiceVoteJpaEntity> findByServiceId(UUID value);

  java.util.List<ServiceVoteJpaEntity> findByAccountId(UUID value);
}
