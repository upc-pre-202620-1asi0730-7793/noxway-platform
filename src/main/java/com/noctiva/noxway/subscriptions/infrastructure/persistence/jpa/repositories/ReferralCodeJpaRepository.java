package com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.repositories;

import com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.entities.ReferralCodeJpaEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReferralCodeJpaRepository extends JpaRepository<ReferralCodeJpaEntity, UUID> {
  java.util.List<ReferralCodeJpaEntity> findByWorkerId(UUID value);

  java.util.List<ReferralCodeJpaEntity> findByCode(String value);
}
