package com.noctiva.noxway.community.domain.repositories;

import com.noctiva.noxway.community.domain.model.aggregates.ServicePoint;
import com.noctiva.noxway.shared.domain.DomainRepository;
import java.util.UUID;

public interface ServicePointRepository extends DomainRepository<ServicePoint> {
  java.util.List<ServicePoint> findByReportedBy(UUID value);

  java.util.List<ServicePoint> findByStatus(String value);
}
