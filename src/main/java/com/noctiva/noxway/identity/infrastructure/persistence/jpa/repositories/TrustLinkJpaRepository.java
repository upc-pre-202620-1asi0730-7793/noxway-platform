package com.noctiva.noxway.identity.infrastructure.persistence.jpa.repositories;

import com.noctiva.noxway.identity.infrastructure.persistence.jpa.entities.TrustLinkJpaEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrustLinkJpaRepository extends JpaRepository<TrustLinkJpaEntity, UUID> {
  java.util.List<TrustLinkJpaEntity> findByWorkerId(UUID value);

  java.util.List<TrustLinkJpaEntity> findByContactId(UUID value);

  java.util.List<TrustLinkJpaEntity> findByStatus(String value);
}
