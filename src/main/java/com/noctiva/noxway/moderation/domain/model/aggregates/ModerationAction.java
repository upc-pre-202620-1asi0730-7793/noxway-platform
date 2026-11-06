package com.noctiva.noxway.moderation.domain.model.aggregates;

import java.time.Instant;
import java.util.UUID;

public record ModerationAction(
    UUID id,
    UUID reportId,
    UUID moderatorId,
    String decision,
    String notes,
    Instant createdAt,
    long version) {
  public static ModerationAction create(
      UUID reportId, UUID moderatorId, String decision, String notes) {
    return new ModerationAction(
        UUID.randomUUID(), reportId, moderatorId, decision, notes, Instant.now(), 0);
  }

  public ModerationAction withReportId(UUID value) {
    return new ModerationAction(id, value, moderatorId, decision, notes, createdAt, version);
  }

  public ModerationAction withModeratorId(UUID value) {
    return new ModerationAction(id, reportId, value, decision, notes, createdAt, version);
  }

  public ModerationAction withDecision(String value) {
    return new ModerationAction(id, reportId, moderatorId, value, notes, createdAt, version);
  }

  public ModerationAction withNotes(String value) {
    return new ModerationAction(id, reportId, moderatorId, decision, value, createdAt, version);
  }
}
