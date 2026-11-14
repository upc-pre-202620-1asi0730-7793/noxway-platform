package com.noctiva.noxway.wellness.interfaces.rest.resources;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record WellnessSummaryResource(
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
    List<DailyRestItemResource> dailyBreakdown) {

  public record DailyRestItemResource(
      LocalDate date, String dayLabel, long minutes, double hours, String status) {}
}
