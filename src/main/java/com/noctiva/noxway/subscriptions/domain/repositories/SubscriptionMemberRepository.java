package com.noctiva.noxway.subscriptions.domain.repositories;

import com.noctiva.noxway.shared.domain.DomainRepository;
import com.noctiva.noxway.subscriptions.domain.model.aggregates.SubscriptionMember;
import java.util.UUID;

public interface SubscriptionMemberRepository extends DomainRepository<SubscriptionMember> {
  java.util.List<SubscriptionMember> findBySubscriptionId(UUID value);

  java.util.List<SubscriptionMember> findByWorkerId(UUID value);

  java.util.List<SubscriptionMember> findByStatus(String value);
}
