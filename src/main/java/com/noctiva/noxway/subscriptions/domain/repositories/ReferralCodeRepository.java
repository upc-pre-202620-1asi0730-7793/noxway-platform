package com.noctiva.noxway.subscriptions.domain.repositories;

import com.noctiva.noxway.shared.domain.DomainRepository;
import com.noctiva.noxway.subscriptions.domain.model.aggregates.ReferralCode;
import java.util.UUID;

public interface ReferralCodeRepository extends DomainRepository<ReferralCode> {
  java.util.List<ReferralCode> findByWorkerId(UUID value);

  java.util.List<ReferralCode> findByCode(String value);
}
