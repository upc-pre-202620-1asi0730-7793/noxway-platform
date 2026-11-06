package com.noctiva.noxway.commuting.domain.repositories;

import com.noctiva.noxway.commuting.domain.model.aggregates.TelemetryPing;
import com.noctiva.noxway.shared.domain.DomainRepository;
import java.util.UUID;

public interface TelemetryPingRepository extends DomainRepository<TelemetryPing> {
  java.util.List<TelemetryPing> findByCommuteId(UUID value);
}
