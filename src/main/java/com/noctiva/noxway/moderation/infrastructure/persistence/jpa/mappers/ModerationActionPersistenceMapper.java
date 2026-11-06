package com.noctiva.noxway.moderation.infrastructure.persistence.jpa.mappers;

import com.noctiva.noxway.moderation.domain.model.aggregates.ModerationAction;
import com.noctiva.noxway.moderation.infrastructure.persistence.jpa.entities.ModerationActionJpaEntity;

public final class ModerationActionPersistenceMapper {
  private ModerationActionPersistenceMapper() {}

  public static ModerationAction toDomain(ModerationActionJpaEntity e) {
    return new ModerationAction(
        e.id, e.reportId, e.moderatorId, e.decision, e.notes, e.createdAt, e.version);
  }

  public static void copy(ModerationAction a, ModerationActionJpaEntity e) {
    e.id = a.id();
    e.reportId = a.reportId();
    e.moderatorId = a.moderatorId();
    e.decision = a.decision();
    e.notes = a.notes();
    e.createdAt = a.createdAt();
  }
}
