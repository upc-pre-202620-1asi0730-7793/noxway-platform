package com.noctiva.noxway.shared.domain;

import java.time.*;

public final class Rules {
  private Rules() {}

  public static void require(boolean ok, String message) {
    if (!ok) throw new RuleViolation(message);
  }

  public static void coordinates(double lat, double lon) {
    require(
        Double.isFinite(lat)
            && Double.isFinite(lon)
            && lat >= -90
            && lat <= 90
            && lon >= -180
            && lon <= 180,
        "Invalid coordinates");
  }

  public static void zone(String zone) {
    try {
      ZoneId.of(zone);
    } catch (Exception e) {
      throw new RuleViolation("Invalid IANA time zone");
    }
  }

  public static void range(Instant start, Instant end) {
    require(start != null && end != null && end.isAfter(start), "End must follow start");
  }
}
