package com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.repositories;

import com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.entities.ReferralRewardJpaEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReferralRewardJpaRepository extends JpaRepository<ReferralRewardJpaEntity, UUID> {
  java.util.List<ReferralRewardJpaEntity> findByWorkerId(UUID value);

  java.util.List<ReferralRewardJpaEntity> findByReferralUseId(UUID value);

  java.util.List<ReferralRewardJpaEntity> findByStatus(String value);
}
