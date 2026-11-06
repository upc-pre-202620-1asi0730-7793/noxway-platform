package com.noctiva.noxway.wellness.domain.model.valueobjects;

import static com.noctiva.noxway.shared.domain.Rules.*;

import java.time.*;

public final class RestRules {
  private RestRules() {}

  public static long minutes(Instant start, Instant end) {
    range(start, end);
    long minutes = Duration.between(start, end).toMinutes();
    require(minutes > 0 && minutes <= 1440, "Rest duration must be 1..1440 minutes");
    return minutes;
  }

  public static boolean overlaps(Instant a, Instant b, Instant c, Instant d) {
    return a.isBefore(d) && c.isBefore(b);
  }

  public static long inWindow(Instant start, Instant end, Instant from, Instant until) {
    Instant a = start.isAfter(from) ? start : from, b = end.isBefore(until) ? end : until;
    return b.isAfter(a) ? Duration.between(a, b).toMinutes() : 0;
  }
}
