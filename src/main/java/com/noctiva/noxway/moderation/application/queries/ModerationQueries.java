package com.noctiva.noxway.moderation.application.queries;

import java.util.UUID;

public final class ModerationQueries {
  private ModerationQueries() {}

  public record GetModerationQueueQuery(String status, int page, int size) {}

  public record GetModerationAuditQuery(UUID reportId) {}
}
