package com.noctiva.noxway.subscriptions.domain.repositories;

import com.noctiva.noxway.shared.domain.DomainRepository;
import com.noctiva.noxway.subscriptions.domain.model.aggregates.ReferralUse;
import java.util.UUID;

public interface ReferralUseRepository extends DomainRepository<ReferralUse> {
  java.util.List<ReferralUse> findByReferralId(UUID value);

  java.util.List<ReferralUse> findByReferredWorkerId(UUID value);
}
