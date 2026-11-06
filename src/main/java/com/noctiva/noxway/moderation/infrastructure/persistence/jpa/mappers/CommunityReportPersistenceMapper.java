package com.noctiva.noxway.moderation.infrastructure.persistence.jpa.mappers;

import com.noctiva.noxway.moderation.domain.model.aggregates.CommunityReport;
import com.noctiva.noxway.moderation.infrastructure.persistence.jpa.entities.CommunityReportJpaEntity;

public final class CommunityReportPersistenceMapper {
  private CommunityReportPersistenceMapper() {}

  public static CommunityReport toDomain(CommunityReportJpaEntity e) {
    return new CommunityReport(
        e.id, e.reportedBy, e.targetType, e.targetId, e.reason, e.status, e.createdAt, e.version);
  }

  public static void copy(CommunityReport a, CommunityReportJpaEntity e) {
    e.id = a.id();
    e.reportedBy = a.reportedBy();
    e.targetType = a.targetType();
    e.targetId = a.targetId();
    e.reason = a.reason();
    e.status = a.status();
    e.createdAt = a.createdAt();
  }
}
