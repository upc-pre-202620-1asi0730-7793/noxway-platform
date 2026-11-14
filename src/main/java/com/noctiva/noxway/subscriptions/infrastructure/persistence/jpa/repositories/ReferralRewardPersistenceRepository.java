package com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.repositories;

import com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.entities.ReferralRewardPersistenceEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReferralRewardPersistenceRepository
    extends JpaRepository<ReferralRewardPersistenceEntity, UUID> {
  List<ReferralRewardPersistenceEntity> findByWorkerId(UUID value);

  List<ReferralRewardPersistenceEntity> findByReferralUseId(UUID value);

  List<ReferralRewardPersistenceEntity> findByStatus(String value);
}
