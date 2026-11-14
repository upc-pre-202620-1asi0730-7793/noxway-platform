package com.noctiva.noxway.community.domain.repositories;

import com.noctiva.noxway.community.domain.model.aggregates.ServiceOpeningInterval;
import com.noctiva.noxway.shared.domain.DomainRepository;
import java.util.UUID;

public interface ServiceOpeningIntervalRepository extends DomainRepository<ServiceOpeningInterval> {
  java.util.List<ServiceOpeningInterval> findByServiceId(UUID value);
}
