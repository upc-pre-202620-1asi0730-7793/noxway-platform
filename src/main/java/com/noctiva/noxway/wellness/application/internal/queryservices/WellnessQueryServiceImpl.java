package com.noctiva.noxway.wellness.application.internal.queryservices;

import com.noctiva.noxway.shared.application.result.ApplicationError;
import com.noctiva.noxway.shared.application.result.Result;
import com.noctiva.noxway.shared.interfaces.rest.Pages;
import com.noctiva.noxway.shared.interfaces.rest.resources.PagedResource;
import com.noctiva.noxway.wellness.application.queries.WellnessQueries.*;
import com.noctiva.noxway.wellness.application.queryservices.WellnessQueryService;
import com.noctiva.noxway.wellness.domain.model.aggregates.RestSession;
import com.noctiva.noxway.wellness.domain.model.aggregates.SleepGoal;
import com.noctiva.noxway.wellness.domain.model.valueobjects.RestRules;
import com.noctiva.noxway.wellness.domain.repositories.RestSessionRepository;
import com.noctiva.noxway.wellness.domain.repositories.SleepGoalRepository;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class WellnessQueryServiceImpl implements WellnessQueryService {

  private final RestSessionRepository restRepository;
  private final SleepGoalRepository goalRepository;
  private final Clock clock;

  public WellnessQueryServiceImpl(
      RestSessionRepository restRepository, SleepGoalRepository goalRepository, Clock clock) {
    this.restRepository = restRepository;
    this.goalRepository = goalRepository;
    this.clock = clock;
  }

  @Override
  public Result<PagedResource<RestSession>, ApplicationError> handle(GetRestSessionsQuery query) {
    var all =
        restRepository.findByWorkerId(query.workerId()).stream()
            .sorted(Comparator.comparing(RestSession::getStartTime).reversed())
            .toList();
    int page = Math.max(0, query.page());
    int size = Pages.clamp(query.size());
    int from = Math.min(page * size, all.size());
    int to = Math.min(from + size, all.size());
    return Result.success(new PagedResource<>(all.subList(from, to), page, size, all.size()));
  }

  @Override
  public Result<SleepGoal, ApplicationError> handle(GetSleepGoalQuery query) {
    var goal =
        goalRepository.findByWorkerId(query.workerId()).stream()
            .findFirst()
            .orElseGet(() -> SleepGoal.create(query.workerId(), 450));
    return Result.success(goal);
  }

  @Override
  public Result<WellnessSummaryView, ApplicationError> handle(GetWellnessSummaryQuery query) {
    ZoneId zone;
    try {
      String tz = (query.timeZone() == null || query.timeZone().isBlank()) ? "America/Lima" : query.timeZone();
      zone = ZoneId.of(tz);
    } catch (Exception ex) {
      return Result.failure(new ApplicationError.ValidationError("Invalid time zone"));
    }

    int targetMinutes =
        goalRepository.findByWorkerId(query.workerId()).stream()
            .findFirst()
            .map(SleepGoal::getTargetMinutes)
            .orElse(450);

    LocalDate today = LocalDate.now(clock.withZone(zone));
    LocalDate fromDate = today.minusDays(6);
    Instant fromInstant = fromDate.atStartOfDay(zone).toInstant();
    Instant untilInstant = today.plusDays(1).atStartOfDay(zone).toInstant();

    var sessions =
        restRepository.findByWorkerId(query.workerId()).stream()
            .sorted(Comparator.comparing(RestSession::getStartTime).reversed())
            .toList();

    long totalMinutes = 0;
    for (var s : sessions) {
      totalMinutes +=
          RestRules.inWindow(s.getStartTime(), s.getEndTime(), fromInstant, untilInstant);
    }

    long weeklyTarget = targetMinutes * 7L;
    long deficitMinutes = Math.max(0, weeklyTarget - totalMinutes);
    long averageDailyMinutes = totalMinutes / 7L;

    List<DailyRestBreakdown> breakdown = new ArrayList<>(7);
    for (int i = 0; i < 7; i++) {
      LocalDate day = fromDate.plusDays(i);
      Instant dayStart = day.atStartOfDay(zone).toInstant();
      Instant dayEnd = day.plusDays(1).atStartOfDay(zone).toInstant();
      long dayMinutes = 0;
      for (var s : sessions) {
        dayMinutes += RestRules.inWindow(s.getStartTime(), s.getEndTime(), dayStart, dayEnd);
      }
      double hours = Math.round((dayMinutes / 60.0) * 10.0) / 10.0;
      String status;
      if (day.equals(today)) {
        status = dayMinutes >= targetMinutes ? "good" : "today";
      } else if (dayMinutes >= targetMinutes) {
        status = "good";
      } else if (dayMinutes >= targetMinutes * 0.75) {
        status = "warning";
      } else {
        status = "danger";
      }
      String label = day.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.forLanguageTag("es"));
      breakdown.add(new DailyRestBreakdown(day, label, dayMinutes, hours, status));
    }

    double avgQuality =
        sessions.stream().mapToInt(RestSession::getQuality).average().orElse(0.0);
    avgQuality = Math.round(avgQuality * 10.0) / 10.0;
    long lastMinutes = sessions.isEmpty() ? 0L : sessions.get(0).durationMinutes();
    String lastNotes = sessions.isEmpty() ? "" : sessions.get(0).getNotes();

    return Result.success(
        new WellnessSummaryView(
            query.workerId(),
            zone.getId(),
            fromDate,
            today,
            targetMinutes,
            totalMinutes,
            averageDailyMinutes,
            deficitMinutes,
            avgQuality,
            lastMinutes,
            lastNotes,
            breakdown));
  }
}
