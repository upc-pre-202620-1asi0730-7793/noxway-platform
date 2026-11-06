package com.noctiva.noxway.commuting.application;

import static com.noctiva.noxway.shared.domain.Rules.*;

import com.noctiva.noxway.commuting.application.commands.CommuteCommands;
import com.noctiva.noxway.commuting.domain.model.aggregates.*;
import com.noctiva.noxway.commuting.domain.model.valueobjects.CommuteRules;
import com.noctiva.noxway.commuting.domain.repositories.*;
import com.noctiva.noxway.commuting.interfaces.acl.CommuteAccess;
import com.noctiva.noxway.identity.interfaces.acl.IdentityAccess;
import com.noctiva.noxway.shared.infrastructure.security.Actor;
import java.time.*;
import java.util.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class CommuteApplicationService implements CommuteAccess {
  private final CommuteRepository commutes;
  private final TelemetryPingRepository pings;
  private final SafetyIncidentRepository incidents;
  private final AlertRepository alerts;
  private final IdentityAccess identity;

  public CommuteApplicationService(
      CommuteRepository c,
      TelemetryPingRepository p,
      SafetyIncidentRepository i,
      AlertRepository a,
      IdentityAccess id) {
    commutes = c;
    pings = p;
    incidents = i;
    alerts = a;
    identity = id;
  }

  private Commute get(UUID id) {
    return commutes
        .findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Commute not found"));
  }

  private Commute ownActive(UUID id) {
    var c = get(id);
    Actor.current().own(c.workerId());
    identity.lockWorker(c.workerId());
    require(c.status().equals("ACTIVE"), "Commute is not active");
    return c;
  }

  public Commute start(CommuteCommands.Start c) {
    var actor = Actor.current();
    actor.worker();
    identity.lockWorker(actor.id());
    CommuteRules.schedule(c.startTime(), c.estimatedArrivalTime(), c.toleranceMinutes());
    require(
        !c.startTime().isAfter(Instant.now().plusSeconds(60)),
        "Start time cannot be in the future");
    require(c.estimatedArrivalTime().isAfter(Instant.now()), "ETA must be in the future");
    require(
        commutes.findByWorkerId(actor.id()).stream().noneMatch(x -> x.status().equals("ACTIVE")),
        "Worker already has an active commute");
    return commutes.save(
        Commute.create(
            actor.id(),
            c.origin(),
            c.destination(),
            c.transport(),
            c.startTime(),
            c.estimatedArrivalTime(),
            c.toleranceMinutes(),
            "ACTIVE",
            null,
            null));
  }

  public List<Commute> own() {
    return commutes.findByWorkerId(Actor.current().id()).stream()
        .sorted(Comparator.comparing(Commute::createdAt).reversed())
        .toList();
  }

  public List<Commute> accompanied(UUID worker) {
    var actor = Actor.current();
    if (!identity.canAccompany(actor.id(), worker))
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accepted trust link required");
    return commutes.findByWorkerId(worker);
  }

  public Commute view(UUID id) {
    var c = get(id);
    var actor = Actor.current();
    if (!actor.id().equals(c.workerId()) && !identity.canAccompany(actor.id(), c.workerId()))
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No consent to view this commute");
    return c;
  }

  public Commute complete(UUID id) {
    var c = ownActive(id);
    Instant now = Instant.now();
    require(!now.isBefore(c.startTime()), "Arrival must follow start");
    var result = commutes.save(c.withStatus("COMPLETED").withArrivalTime(now));
    notifyContacts(c, null, "Safe arrival confirmed");
    return result;
  }

  public Commute cancel(UUID id) {
    var c = ownActive(id);
    var result = commutes.save(c.withStatus("CANCELLED"));
    notifyContacts(c, null, "Commute cancelled by worker");
    return result;
  }

  public Commute delay(UUID id, int minutes) {
    var c = ownActive(id);
    Instant base = c.delayUntil() == null ? c.estimatedArrivalTime() : c.delayUntil();
    Instant until = base.plusSeconds(minutes * 60L);
    require(
        Duration.between(c.estimatedArrivalTime(), until).toMinutes() <= 120,
        "Total extension cannot exceed 120 minutes");
    return commutes.save(c.withDelayUntil(until));
  }

  public TelemetryPing ping(UUID id, CommuteCommands.Ping p) {
    var c = ownActive(id);
    coordinates(p.latitude(), p.longitude());
    require(
        !p.recordedAt().isBefore(c.startTime())
            && !p.recordedAt().isAfter(Instant.now().plusSeconds(60)),
        "Telemetry time outside active commute");
    var last =
        pings.findByCommuteId(id).stream().max(Comparator.comparing(TelemetryPing::recordedAt));
    require(
        last.isEmpty() || p.recordedAt().isAfter(last.get().recordedAt()),
        "Telemetry must be chronological");
    return pings.save(
        TelemetryPing.create(id, p.latitude(), p.longitude(), p.speed(), p.recordedAt()));
  }

  public List<TelemetryPing> telemetry(UUID id) {
    var c = view(id);
    require(
        c.status().equals("ACTIVE") || Actor.current().id().equals(c.workerId()),
        "Location visibility ends after the commute");
    return pings.findByCommuteId(id).stream()
        .sorted(Comparator.comparing(TelemetryPing::recordedAt))
        .toList();
  }

  public SafetyIncident incident(UUID id, CommuteCommands.Incident p) {
    var c = ownActive(id);
    var result =
        incidents.save(
            SafetyIncident.create(
                id, c.workerId(), p.type(), p.description(), "OPEN", Instant.now()));
    notifyContacts(c, result.id(), p.type() + ": " + p.description());
    return result;
  }

  public List<SafetyIncident> incidents(UUID id) {
    view(id);
    return incidents.findByCommuteId(id);
  }

  public SafetyIncident resolve(UUID incidentId) {
    var i =
        incidents
            .findById(incidentId)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Incident not found"));
    Actor.current().own(i.workerId());
    require(i.status().equals("OPEN"), "Incident already resolved");
    return incidents.save(i.withStatus("RESOLVED"));
  }

  public List<Alert> myAlerts() {
    return alerts.findByContactId(Actor.current().id()).stream()
        .filter(
            a -> {
              var c = get(a.commuteId());
              return identity.canAccompany(Actor.current().id(), c.workerId());
            })
        .toList();
  }

  public Alert readAlert(UUID id) {
    var a =
        alerts
            .findById(id)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Alert not found"));
    Actor.current().own(a.contactId());
    require(
        identity.canAccompany(a.contactId(), get(a.commuteId()).workerId()),
        "Trust link no longer active");
    require(a.channel().equals("IN_APP"), "Only in-app alerts may be acknowledged here");
    return alerts.save(a.withStatus("READ").withDeliveredAt(Instant.now()));
  }

  private void notifyContacts(Commute c, UUID incident, String message) {
    for (var contact : identity.contactsOf(c.workerId()))
      for (var channel : identity.channelsFor(contact))
        alerts.save(
            Alert.create(
                c.id(),
                incident,
                contact,
                channel,
                channel.equals("IN_APP") ? "AVAILABLE" : "PENDING_PROVIDER",
                message,
                null));
  }

  public void evaluateOverdue() {
    for (var candidate : commutes.findByStatus("ACTIVE")) {
      identity.lockWorker(candidate.workerId());
      var c = get(candidate.id());
      if (c.status().equals("ACTIVE")
          && CommuteRules.overdue(
              c.estimatedArrivalTime(), c.toleranceMinutes(), c.delayUntil(), Instant.now())
          && incidents.findByCommuteId(c.id()).stream()
              .noneMatch(i -> i.type().equals("DELAY") && i.status().equals("OPEN"))) {
        var i =
            incidents.save(
                SafetyIncident.create(
                    c.id(),
                    c.workerId(),
                    "DELAY",
                    "ETA and tolerance exceeded without safe-arrival confirmation",
                    "OPEN",
                    Instant.now()));
        notifyContacts(c, i.id(), "Possible incident: arrival is overdue");
      }
    }
  }

  public void requireCompletedOwn(UUID id, UUID worker) {
    var c = get(id);
    require(
        c.workerId().equals(worker) && c.status().equals("COMPLETED"),
        "Rating requires a completed commute owned by worker");
  }
}
