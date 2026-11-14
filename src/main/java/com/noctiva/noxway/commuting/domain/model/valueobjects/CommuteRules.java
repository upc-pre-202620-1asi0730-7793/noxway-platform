package com.noctiva.noxway.commuting.domain.model.valueobjects;

import static com.noctiva.noxway.shared.domain.Rules.*;

import java.time.*;

public final class CommuteRules {
  private CommuteRules() {}

  public static void schedule(Instant start, Instant eta, int tolerance) {
    range(start, eta);
    require(Duration.between(start, eta).toHours() < 24, "Commute must be shorter than 24 hours");
    require(tolerance >= 0 && tolerance <= 120, "Tolerance must be 0..120 minutes");
  }

  public static boolean overdue(Instant eta, int tolerance, Instant delayUntil, Instant now) {
    Instant deadline = eta.plusSeconds(tolerance * 60L);
    if (delayUntil != null && delayUntil.isAfter(deadline)) deadline = delayUntil;
    return now.isAfter(deadline);
  }
}
