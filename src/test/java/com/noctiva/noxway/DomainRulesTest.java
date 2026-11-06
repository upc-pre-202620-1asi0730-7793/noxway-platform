package com.noctiva.noxway;

import static org.junit.jupiter.api.Assertions.*;

import com.noctiva.noxway.community.domain.model.aggregates.ServiceOpeningInterval;
import com.noctiva.noxway.community.domain.model.valueobjects.OpeningHours;
import com.noctiva.noxway.commuting.domain.model.valueobjects.CommuteRules;
import com.noctiva.noxway.wellness.domain.model.valueobjects.RestRules;
import java.time.*;
import java.util.*;
import org.junit.jupiter.api.Test;

class DomainRulesTest {
  @Test
  void previousDayAndWeekBoundary() {
    var h = ServiceOpeningInterval.create(UUID.randomUUID(), 7, "22:00", "06:00", true, false);
    assertTrue(
        OpeningHours.isOpen(List.of(h), "America/Lima", Instant.parse("2026-11-02T06:00:00Z")));
    assertFalse(
        OpeningHours.isOpen(List.of(h), "America/Lima", Instant.parse("2026-11-02T11:00:00Z")));
  }

  @Test
  void continuousDayEndsAtNextMidnight() {
    var h = ServiceOpeningInterval.create(UUID.randomUUID(), 1, "00:00", "00:00", false, true);
    assertTrue(
        OpeningHours.isOpen(List.of(h), "America/Lima", Instant.parse("2026-11-03T04:59:00Z")));
    assertFalse(
        OpeningHours.isOpen(List.of(h), "America/Lima", Instant.parse("2026-11-03T05:00:00Z")));
  }

  @Test
  void restAndCommuteCrossMidnight() {
    Instant a = Instant.parse("2026-11-02T23:00:00Z"), b = Instant.parse("2026-11-03T06:00:00Z");
    assertEquals(420, RestRules.minutes(a, b));
    assertThrows(RuntimeException.class, () -> RestRules.minutes(b, a));
    CommuteRules.schedule(a, b, 10);
    assertFalse(CommuteRules.overdue(b, 10, null, b.plusSeconds(600)));
    assertTrue(CommuteRules.overdue(b, 10, null, b.plusSeconds(601)));
  }

  @Test
  void adjacentRestDoesNotOverlap() {
    Instant a = Instant.parse("2026-11-02T20:00:00Z"),
        b = a.plusSeconds(3600),
        c = b.plusSeconds(3600);
    assertFalse(RestRules.overlaps(a, b, b, c));
    assertEquals(30, RestRules.inWindow(a, b, a.plusSeconds(1800), c));
  }

  @Test
  void rejectInvalidOpeningHours() {
    assertThrows(
        RuntimeException.class, () -> OpeningHours.validate(1, "22:00", "06:00", false, false));
    assertThrows(
        RuntimeException.class, () -> OpeningHours.validate(8, "00:00", "00:00", false, true));
    assertFalse(OpeningHours.isOpen(List.of(), "America/Lima", Instant.now()));
  }
}
