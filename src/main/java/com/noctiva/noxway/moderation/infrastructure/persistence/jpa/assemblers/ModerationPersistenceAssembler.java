package com.noctiva.noxway.moderation.infrastructure.persistence.jpa.assemblers;

import com.noctiva.noxway.moderation.domain.model.aggregates.CommunityReport;
import com.noctiva.noxway.moderation.domain.model.aggregates.ModerationAction;
import com.noctiva.noxway.moderation.infrastructure.persistence.jpa.entities.CommunityReportPersistenceEntity;
import com.noctiva.noxway.moderation.infrastructure.persistence.jpa.entities.ModerationActionPersistenceEntity;

public final class ModerationPersistenceAssembler {
  private ModerationPersistenceAssembler() {}

  public static CommunityReport toDomain(CommunityReportPersistenceEntity e) {
    return CommunityReport.reconstitute(
        e.getId(),
        e.getReportedBy(),
        e.getTargetType(),
        e.getTargetId(),
        e.getReason(),
        e.getStatus(),
        e.getCreatedAt(),
        e.getVersion());
  }

  public static CommunityReportPersistenceEntity toPersistence(CommunityReport r) {
    var e = new CommunityReportPersistenceEntity();
    e.setId(r.getId());
    e.setReportedBy(r.getReportedBy());
    e.setTargetType(r.getTargetType());
    e.setTargetId(r.getTargetId());
    e.setReason(r.getReason());
    e.setStatus(r.getStatus());
    e.setCreatedAt(r.getCreatedAt());
    e.setVersion(r.getVersion());
    return e;
  }

  public static ModerationAction toDomain(ModerationActionPersistenceEntity e) {
    return ModerationAction.reconstitute(
        e.getId(),
        e.getReportId(),
        e.getModeratorId(),
        e.getDecision(),
        e.getNotes(),
        e.getCreatedAt(),
        e.getVersion());
  }

  public static ModerationActionPersistenceEntity toPersistence(ModerationAction a) {
    var e = new ModerationActionPersistenceEntity();
    e.setId(a.getId());
    e.setReportId(a.getReportId());
    e.setModeratorId(a.getModeratorId());
    e.setDecision(a.getDecision());
    e.setNotes(a.getNotes());
    e.setCreatedAt(a.getCreatedAt());
    e.setVersion(a.getVersion());
    return e;
  }
}
