package com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.repositories;

import com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.entities.ReferralUsePersistenceEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReferralUsePersistenceRepository
    extends JpaRepository<ReferralUsePersistenceEntity, UUID> {
  List<ReferralUsePersistenceEntity> findByReferralId(UUID value);

  List<ReferralUsePersistenceEntity> findByReferredWorkerId(UUID value);
}
