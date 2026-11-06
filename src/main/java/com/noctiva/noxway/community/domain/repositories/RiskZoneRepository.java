package com.noctiva.noxway.community.domain.repositories;

import com.noctiva.noxway.community.domain.model.aggregates.RiskZone;
import com.noctiva.noxway.shared.domain.DomainRepository;
import java.util.UUID;

public interface RiskZoneRepository extends DomainRepository<RiskZone> {
  java.util.List<RiskZone> findByReportedBy(UUID value);

  java.util.List<RiskZone> findByStatus(String value);
}
