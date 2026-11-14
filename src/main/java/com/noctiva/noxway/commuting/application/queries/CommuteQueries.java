package com.noctiva.noxway.commuting.application.queries;

import java.util.UUID;

public final class CommuteQueries {
  private CommuteQueries() {}

  public record GetMyCommutesQuery(UUID workerId, int page, int size) {}

  public record GetCommuteByIdQuery(UUID commuteId, UUID actorId, boolean privileged) {}

  public record GetTelemetryPingsQuery(UUID commuteId, UUID actorId, boolean privileged) {}

  public record GetCommuteIncidentsQuery(UUID commuteId, UUID actorId, boolean privileged) {}

  public record GetCompanionLiveQuery(UUID contactId) {}

  public record GetCompanionAlertsQuery(UUID contactId, int page, int size) {}
}
