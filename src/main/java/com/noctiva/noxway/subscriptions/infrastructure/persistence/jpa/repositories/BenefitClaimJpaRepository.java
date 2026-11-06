package com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.repositories;

import com.noctiva.noxway.subscriptions.infrastructure.persistence.jpa.entities.BenefitClaimJpaEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BenefitClaimJpaRepository extends JpaRepository<BenefitClaimJpaEntity, UUID> {
  java.util.List<BenefitClaimJpaEntity> findByWorkerId(UUID value);

  java.util.List<BenefitClaimJpaEntity> findByBenefitId(UUID value);

  java.util.List<BenefitClaimJpaEntity> findByCode(String value);
}
