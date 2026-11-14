package com.noctiva.noxway.commuting.domain.repositories;

import com.noctiva.noxway.commuting.domain.model.aggregates.Commute;
import com.noctiva.noxway.shared.domain.DomainRepository;
import java.util.UUID;

public interface CommuteRepository extends DomainRepository<Commute> {
  java.util.List<Commute> findByWorkerId(UUID value);

  java.util.List<Commute> findByStatus(String value);
}
