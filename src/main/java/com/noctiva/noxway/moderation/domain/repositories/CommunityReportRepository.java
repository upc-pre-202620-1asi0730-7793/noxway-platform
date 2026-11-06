package com.noctiva.noxway.moderation.domain.repositories;

import com.noctiva.noxway.moderation.domain.model.aggregates.CommunityReport;
import com.noctiva.noxway.shared.domain.DomainRepository;
import java.util.UUID;

public interface CommunityReportRepository extends DomainRepository<CommunityReport> {
  java.util.List<CommunityReport> findByReportedBy(UUID value);

  java.util.List<CommunityReport> findByTargetId(UUID value);

  java.util.List<CommunityReport> findByStatus(String value);
}
