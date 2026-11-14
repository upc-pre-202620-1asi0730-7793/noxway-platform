package com.noctiva.noxway.commuting.domain.repositories;

import com.noctiva.noxway.commuting.domain.model.aggregates.Alert;
import com.noctiva.noxway.shared.domain.DomainRepository;
import java.util.UUID;

public interface AlertRepository extends DomainRepository<Alert> {
  java.util.List<Alert> findByCommuteId(UUID value);

  java.util.List<Alert> findByIncidentId(UUID value);

  java.util.List<Alert> findByContactId(UUID value);

  java.util.List<Alert> findByStatus(String value);
}
