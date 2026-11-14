package com.noctiva.noxway.subscriptions.domain.repositories;

import com.noctiva.noxway.shared.domain.DomainRepository;
import com.noctiva.noxway.subscriptions.domain.model.aggregates.Plan;

public interface PlanRepository extends DomainRepository<Plan> {
  java.util.List<Plan> findByCode(String value);
}
