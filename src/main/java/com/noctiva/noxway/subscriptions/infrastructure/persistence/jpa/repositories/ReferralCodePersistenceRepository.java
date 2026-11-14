package com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.repositories;

import com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.entities.ReferralCodePersistenceEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReferralCodePersistenceRepository
    extends JpaRepository<ReferralCodePersistenceEntity, UUID> {
  List<ReferralCodePersistenceEntity> findByWorkerId(UUID value);

  List<ReferralCodePersistenceEntity> findByCode(String value);
}
