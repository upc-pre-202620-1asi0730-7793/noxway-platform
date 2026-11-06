package com.noctiva.noxway.community.domain.model.valueobjects;

import static com.noctiva.noxway.shared.domain.Rules.*;

import com.noctiva.noxway.community.domain.model.aggregates.ServiceOpeningInterval;
import java.time.*;
import java.util.*;

public final class OpeningHours {
  private OpeningHours() {}

  public static void validate(int day, String opens, String closes, boolean next, boolean full) {
    require(day >= 1 && day <= 7, "Day must be 1..7");
    try {
      var a = LocalTime.parse(opens);
      var b = LocalTime.parse(closes);
      if (full)
        require(
            a.equals(LocalTime.MIDNIGHT) && b.equals(LocalTime.MIDNIGHT) && !next,
            "24-hour interval must use 00:00/00:00 without closesNextDay");
      else
        require(
            next ? b.isBefore(a) || b.equals(a) : b.isAfter(a),
            "Invalid interval; mark closesNextDay for overnight opening");
    } catch (DateTimeException | NullPointerException e) {
      throw new com.noctiva.noxway.shared.domain.RuleViolation("Use HH:mm opening times");
    }
  }

  public static boolean isOpen(List<ServiceOpeningInterval> hours, String zone, Instant at) {
    var local = at.atZone(ZoneId.of(zone));
    var date = local.toLocalDate();
    for (var h : hours) {
      for (int daysBack = 0; daysBack <= 1; daysBack++) {
        var d = date.minusDays(daysBack);
        if (d.getDayOfWeek().getValue() != h.dayOfWeek()) continue;
        var start = d.atTime(LocalTime.parse(h.opensAt())).atZone(ZoneId.of(zone)).toInstant();
        var end =
            (h.is24Hours()
                    ? d.plusDays(1).atStartOfDay()
                    : d.plusDays(h.closesNextDay() ? 1 : 0).atTime(LocalTime.parse(h.closesAt())))
                .atZone(ZoneId.of(zone))
                .toInstant();
        if (!at.isBefore(start) && at.isBefore(end)) return true;
      }
    }
    return false;
  }

  public static double distanceKm(double lat1, double lon1, double lat2, double lon2) {
    double a =
        Math.pow(Math.sin(Math.toRadians(lat2 - lat1) / 2), 2)
            + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2))
                * Math.pow(Math.sin(Math.toRadians(lon2 - lon1) / 2), 2);
    return 6371 * 2 * Math.asin(Math.sqrt(Math.min(1, a)));
  }
}
