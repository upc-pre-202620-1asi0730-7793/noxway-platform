package com.noctiva.noxway.subscriptions.domain.repositories;

import com.noctiva.noxway.shared.domain.DomainRepository;
import com.noctiva.noxway.subscriptions.domain.model.aggregates.Subscription;
import java.util.UUID;

public interface SubscriptionRepository extends DomainRepository<Subscription> {
  java.util.List<Subscription> findByWorkerId(UUID value);

  java.util.List<Subscription> findByPlanId(UUID value);

  java.util.List<Subscription> findByStatus(String value);
}
