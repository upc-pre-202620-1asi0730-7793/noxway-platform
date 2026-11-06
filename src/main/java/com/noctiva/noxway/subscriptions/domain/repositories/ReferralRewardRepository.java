package com.noctiva.noxway.subscriptions.domain.repositories;

import com.noctiva.noxway.shared.domain.DomainRepository;
import com.noctiva.noxway.subscriptions.domain.model.aggregates.ReferralReward;
import java.util.UUID;

public interface ReferralRewardRepository extends DomainRepository<ReferralReward> {
  java.util.List<ReferralReward> findByWorkerId(UUID value);

  java.util.List<ReferralReward> findByReferralUseId(UUID value);

  java.util.List<ReferralReward> findByStatus(String value);
}
