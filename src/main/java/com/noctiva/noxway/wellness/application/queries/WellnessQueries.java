package com.noctiva.noxway.wellness.application.queries;

import java.util.UUID;

public final class WellnessQueries {
  private WellnessQueries() {}

  public record GetRestSessionsQuery(UUID workerId, int page, int size) {}

  public record GetSleepGoalQuery(UUID workerId) {}

  public record GetWellnessSummaryQuery(UUID workerId, String timeZone) {}
}
