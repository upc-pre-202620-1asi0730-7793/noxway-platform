package com.noctiva.noxway.subscriptions.domain.repositories;

import com.noctiva.noxway.shared.domain.DomainRepository;
import com.noctiva.noxway.subscriptions.domain.model.aggregates.BenefitClaim;
import java.util.UUID;

public interface BenefitClaimRepository extends DomainRepository<BenefitClaim> {
  java.util.List<BenefitClaim> findByWorkerId(UUID value);

  java.util.List<BenefitClaim> findByBenefitId(UUID value);

  java.util.List<BenefitClaim> findByCode(String value);
}
