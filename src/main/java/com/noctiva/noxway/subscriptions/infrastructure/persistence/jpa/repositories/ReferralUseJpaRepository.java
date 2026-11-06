package com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.repositories;

import com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.entities.ReferralUseJpaEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReferralUseJpaRepository extends JpaRepository<ReferralUseJpaEntity, UUID> {
  java.util.List<ReferralUseJpaEntity> findByReferralId(UUID value);

  java.util.List<ReferralUseJpaEntity> findByReferredWorkerId(UUID value);
}
