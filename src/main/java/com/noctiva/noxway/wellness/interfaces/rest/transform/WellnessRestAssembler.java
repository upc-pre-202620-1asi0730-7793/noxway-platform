package com.noctiva.noxway.wellness.interfaces.rest.transform;

import com.noctiva.noxway.shared.interfaces.rest.resources.PagedResource;
import com.noctiva.noxway.wellness.application.commands.WellnessCommands.*;
import com.noctiva.noxway.wellness.application.queryservices.WellnessQueryService.WellnessSummaryView;
import com.noctiva.noxway.wellness.domain.model.aggregates.RestSession;
import com.noctiva.noxway.wellness.domain.model.aggregates.SleepGoal;
import com.noctiva.noxway.wellness.interfaces.rest.resources.*;
import java.util.UUID;

public final class WellnessRestAssembler {
  private WellnessRestAssembler() {}

  public static LogRestSessionCommand toCommand(UUID workerId, LogRestSessionResource r) {
    return new LogRestSessionCommand(workerId, r.startTime(), r.endTime(), r.quality(), r.notes());
  }

  public static UpdateSleepGoalCommand toCommand(UUID workerId, UpdateSleepGoalResource r) {
    return new UpdateSleepGoalCommand(workerId, r.targetMinutes());
  }

  public static RestSessionResource toResource(RestSession s) {
    return new RestSessionResource(
        s.getId(),
        s.getWorkerId(),
        s.getStartTime(),
        s.getEndTime(),
        s.durationMinutes(),
        s.getQuality(),
        s.getNotes(),
        s.getCreatedAt());
  }

  public static PagedResource<RestSessionResource> toPagedResource(PagedResource<RestSession> page) {
    return new PagedResource<>(
        page.items().stream().map(WellnessRestAssembler::toResource).toList(),
        page.page(),
        page.size(),
        page.total());
  }

  public static SleepGoalResource toResource(SleepGoal g) {
    return new SleepGoalResource(g.getId(), g.getWorkerId(), g.getTargetMinutes(), g.getCreatedAt());
  }

  public static WellnessSummaryResource toResource(WellnessSummaryView view) {
    var items =
        view.dailyBreakdown().stream()
            .map(
                d ->
                    new WellnessSummaryResource.DailyRestItemResource(
                        d.date(), d.dayLabel(), d.minutes(), d.hours(), d.status()))
            .toList();
    return new WellnessSummaryResource(
        view.workerId(),
        view.timeZone(),
        view.fromDate(),
        view.toDate(),
        view.targetDailyMinutes(),
        view.totalMinutes(),
        view.averageDailyMinutes(),
        view.deficitMinutes(),
        view.averageQuality(),
        view.lastSessionMinutes(),
        view.lastSessionNotes(),
        items);
  }
}
