package com.noctiva.noxway.wellness.application.queryservices;

import com.noctiva.noxway.shared.application.result.ApplicationError;
import com.noctiva.noxway.shared.application.result.Result;
import com.noctiva.noxway.shared.interfaces.rest.resources.PagedResource;
import com.noctiva.noxway.wellness.application.queries.WellnessQueries.*;
import com.noctiva.noxway.wellness.domain.model.aggregates.RestSession;
import com.noctiva.noxway.wellness.domain.model.aggregates.SleepGoal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface WellnessQueryService {

  Result<PagedResource<RestSession>, ApplicationError> handle(GetRestSessionsQuery query);

  Result<SleepGoal, ApplicationError> handle(GetSleepGoalQuery query);

  Result<WellnessSummaryView, ApplicationError> handle(GetWellnessSummaryQuery query);

  record DailyRestBreakdown(
      LocalDate date, String dayLabel, long minutes, double hours, String status) {}

  record WellnessSummaryView(
      UUID workerId,
      String timeZone,
      LocalDate fromDate,
      LocalDate toDate,
      int targetDailyMinutes,
      long totalMinutes,
      long averageDailyMinutes,
      long deficitMinutes,
      double averageQuality,
      long lastSessionMinutes,
      String lastSessionNotes,
      List<DailyRestBreakdown> dailyBreakdown) {}
}
