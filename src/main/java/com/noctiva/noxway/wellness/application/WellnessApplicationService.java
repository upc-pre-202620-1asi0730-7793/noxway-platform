package com.noctiva.noxway.wellness.application;

import static com.noctiva.noxway.shared.domain.Rules.*;

import com.noctiva.noxway.identity.interfaces.acl.IdentityAccess;
import com.noctiva.noxway.shared.infrastructure.security.Actor;
import com.noctiva.noxway.wellness.application.commands.WellnessCommands;
import com.noctiva.noxway.wellness.domain.model.aggregates.*;
import com.noctiva.noxway.wellness.domain.model.valueobjects.RestRules;
import com.noctiva.noxway.wellness.domain.repositories.*;
import java.time.*;
import java.util.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class WellnessApplicationService {
  private final RestSessionRepository rests;
  private final SleepGoalRepository goals;
  private final IdentityAccess identity;

  public WellnessApplicationService(
      RestSessionRepository r, SleepGoalRepository g, IdentityAccess i) {
    rests = r;
    goals = g;
    identity = i;
  }

  public RestSession register(WellnessCommands.Rest c) {
    var who = Actor.current();
    who.worker();
    identity.lockWorker(who.id());
    RestRules.minutes(c.startTime(), c.endTime());
    require(!c.endTime().isAfter(Instant.now()), "Cannot register future rest");
    require(
        rests.findByWorkerId(who.id()).stream()
            .noneMatch(
                r -> RestRules.overlaps(c.startTime(), c.endTime(), r.startTime(), r.endTime())),
        "Rest sessions cannot overlap");
    return rests.save(
        RestSession.create(who.id(), c.startTime(), c.endTime(), c.quality(), c.notes()));
  }

  public List<RestSession> list() {
    Actor.current().worker();
    return rests.findByWorkerId(Actor.current().id()).stream()
        .sorted(Comparator.comparing(RestSession::startTime).reversed())
        .toList();
  }

  public void delete(UUID id) {
    var r =
        rests
            .findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rest not found"));
    Actor.current().own(r.workerId());
    identity.lockWorker(r.workerId());
    rests.delete(id);
  }

  public SleepGoal goal(int minutes) {
    var who = Actor.current();
    who.worker();
    identity.lockWorker(who.id());
    var old = goals.findByWorkerId(who.id()).stream().findFirst();
    return goals.save(
        old.map(g -> g.withTargetMinutes(minutes))
            .orElseGet(() -> SleepGoal.create(who.id(), minutes)));
  }

  public record DailyRest(LocalDate date, long minutes, int targetMinutes) {}

  public record Summary(
      List<DailyRest> days,
      long totalMinutes,
      double averageMinutes,
      long deficitMinutes,
      List<String> suggestions,
      String disclaimer) {}

  public Summary summary(LocalDate through, String timeZone) {
    Actor.current().worker();
    zone(timeZone);
    var zone = ZoneId.of(timeZone);
    LocalDate last = through == null ? LocalDate.now(zone) : through;
    require(!last.isAfter(LocalDate.now(zone)), "Summary date cannot be in the future");
    var all = rests.findByWorkerId(Actor.current().id());
    int target =
        goals.findByWorkerId(Actor.current().id()).stream()
            .findFirst()
            .map(SleepGoal::targetMinutes)
            .orElse(450);
    List<DailyRest> days = new ArrayList<>();
    long total = 0, deficit = 0;
    for (int i = 6; i >= 0; i--) {
      var d = last.minusDays(i);
      Instant from = d.atStartOfDay(zone).toInstant(),
          until = d.plusDays(1).atStartOfDay(zone).toInstant();
      long minutes =
          all.stream()
              .mapToLong(r -> RestRules.inWindow(r.startTime(), r.endTime(), from, until))
              .sum();
      days.add(new DailyRest(d, minutes, target));
      total += minutes;
      deficit += Math.max(0, target - minutes);
    }
    var tips = new ArrayList<String>();
    tips.add("Adapt light and noise to your chosen rest period, by day or night.");
    tips.add("Review caffeine intake before the time you intend to sleep.");
    if (deficit > 0)
      tips.add(
          "Recorded rest is below your personal target; review missing logs and your rest"
              + " schedule.");
    return new Summary(
        days,
        total,
        Math.round(total / 7.0 * 100) / 100.0,
        deficit,
        tips,
        "Informational record, not medical advice or a diagnosis. Missing logs do not prove lack of"
            + " sleep.");
  }
}
