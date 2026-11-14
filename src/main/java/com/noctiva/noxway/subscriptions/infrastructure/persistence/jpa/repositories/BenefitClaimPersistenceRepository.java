package com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.repositories;

import com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.entities.BenefitClaimPersistenceEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BenefitClaimPersistenceRepository
    extends JpaRepository<BenefitClaimPersistenceEntity, UUID> {
  List<BenefitClaimPersistenceEntity> findByWorkerId(UUID value);

  List<BenefitClaimPersistenceEntity> findByBenefitId(UUID value);

  List<BenefitClaimPersistenceEntity> findByCode(String value);
}
