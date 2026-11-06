package com.noctiva.noxway.wellness.domain.repositories;

import com.noctiva.noxway.shared.domain.DomainRepository;
import com.noctiva.noxway.wellness.domain.model.aggregates.SleepGoal;
import java.util.UUID;

public interface SleepGoalRepository extends DomainRepository<SleepGoal> {
  java.util.List<SleepGoal> findByWorkerId(UUID value);
}
