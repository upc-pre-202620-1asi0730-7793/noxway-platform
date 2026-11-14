package com.noctiva.noxway.commuting.domain.repositories;

import com.noctiva.noxway.commuting.domain.model.aggregates.SafetyIncident;
import com.noctiva.noxway.shared.domain.DomainRepository;
import java.util.UUID;

public interface SafetyIncidentRepository extends DomainRepository<SafetyIncident> {
  java.util.List<SafetyIncident> findByCommuteId(UUID value);

  java.util.List<SafetyIncident> findByWorkerId(UUID value);

  java.util.List<SafetyIncident> findByStatus(String value);
}
