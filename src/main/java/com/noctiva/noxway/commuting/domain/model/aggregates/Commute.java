package com.noctiva.noxway.commuting.domain.model.aggregates;

import static com.noctiva.noxway.shared.domain.Rules.require;

import com.noctiva.noxway.commuting.domain.model.events.CommuteArrivedEvent;
import com.noctiva.noxway.commuting.domain.model.events.CommuteDelayedEvent;
import com.noctiva.noxway.commuting.domain.model.events.CommuteStartedEvent;
import com.noctiva.noxway.commuting.domain.model.valueobjects.CommuteRules;
import com.noctiva.noxway.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class Commute extends AbstractDomainAggregateRoot<Commute> {

  private final UUID id;
  private final UUID workerId;
  private final String origin;
  private final String destination;
  private final String transport;
  private final Instant startTime;
  private final Instant estimatedArrivalTime;
  private final int toleranceMinutes;
  private String status;
  private Instant arrivalTime;
  private Instant delayUntil;
  private final Instant createdAt;
  private final long version;

  private Commute(
      UUID id,
      UUID workerId,
      String origin,
      String destination,
      String transport,
      Instant startTime,
      Instant estimatedArrivalTime,
      int toleranceMinutes,
      String status,
      Instant arrivalTime,
      Instant delayUntil,
      Instant createdAt,
      long version) {
    this.id = id;
    this.workerId = workerId;
    this.origin = origin;
    this.destination = destination;
    this.transport = transport;
    this.startTime = startTime;
    this.estimatedArrivalTime = estimatedArrivalTime;
    this.toleranceMinutes = toleranceMinutes;
    this.status = status;
    this.arrivalTime = arrivalTime;
    this.delayUntil = delayUntil;
    this.createdAt = createdAt;
    this.version = version;
  }

  public static Commute start(
      UUID workerId,
      String origin,
      String destination,
      String transport,
      Instant startTime,
      Instant estimatedArrivalTime,
      int toleranceMinutes) {
    Objects.requireNonNull(workerId);
    require(origin != null && !origin.isBlank(), "Origin is required");
    require(destination != null && !destination.isBlank(), "Destination is required");
    require(transport != null && !transport.isBlank(), "Transport mode is required");
    CommuteRules.schedule(startTime, estimatedArrivalTime, toleranceMinutes);
    var commute =
        new Commute(
            UUID.randomUUID(),
            workerId,
            origin.trim(),
            destination.trim(),
            transport.trim(),
            startTime,
            estimatedArrivalTime,
            toleranceMinutes,
            "ACTIVE",
            null,
            null,
            Instant.now(),
            0L);
    commute.registerEvent(
        new CommuteStartedEvent(
            commute.id, workerId, commute.destination, estimatedArrivalTime, commute.createdAt));
    return commute;
  }

  public static Commute reconstitute(
      UUID id,
      UUID workerId,
      String origin,
      String destination,
      String transport,
      Instant startTime,
      Instant estimatedArrivalTime,
      int toleranceMinutes,
      String status,
      Instant arrivalTime,
      Instant delayUntil,
      Instant createdAt,
      long version) {
    return new Commute(
        id,
        workerId,
        origin,
        destination,
        transport,
        startTime,
        estimatedArrivalTime,
        toleranceMinutes,
        status,
        arrivalTime,
        delayUntil,
        createdAt,
        version);
  }

  public void extendDelay(int minutes, Instant now) {
    require(!"ARRIVED".equals(status), "Commute has already arrived");
    require(minutes >= 1 && minutes <= 120, "Delay extension must be 1..120 minutes");
    Instant base = delayUntil != null && delayUntil.isAfter(now)
        ? delayUntil
        : estimatedArrivalTime.plusSeconds(toleranceMinutes * 60L);
    if (base.isBefore(now)) {
      base = now;
    }
    this.delayUntil = base.plusSeconds(minutes * 60L);
    this.status = "DELAYED";
    registerEvent(new CommuteDelayedEvent(id, workerId, minutes, this.delayUntil, now));
  }

  public void markOverdue(Instant now) {
    require("ACTIVE".equals(status) || "DELAYED".equals(status), "Commute cannot become overdue");
    if (CommuteRules.overdue(estimatedArrivalTime, toleranceMinutes, delayUntil, now)) {
      this.status = "OVERDUE";
    }
  }

  public void arrive(Instant now) {
    require(!"ARRIVED".equals(status), "Commute has already arrived");
    this.arrivalTime = now;
    this.status = "ARRIVED";
    registerEvent(new CommuteArrivedEvent(id, workerId, now, now));
  }

  public boolean isOngoing() {
    return !"ARRIVED".equals(status);
  }

  public boolean isOverdueAt(Instant now) {
    return isOngoing()
        && CommuteRules.overdue(estimatedArrivalTime, toleranceMinutes, delayUntil, now);
  }

  public UUID getId() {
    return id;
  }

  public UUID getWorkerId() {
    return workerId;
  }

  public String getOrigin() {
    return origin;
  }

  public String getDestination() {
    return destination;
  }

  public String getTransport() {
    return transport;
  }

  public Instant getStartTime() {
    return startTime;
  }

  public Instant getEstimatedArrivalTime() {
    return estimatedArrivalTime;
  }

  public int getToleranceMinutes() {
    return toleranceMinutes;
  }

  public String getStatus() {
    return status;
  }

  public Instant getArrivalTime() {
    return arrivalTime;
  }

  public Instant getDelayUntil() {
    return delayUntil;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public long getVersion() {
    return version;
  }
}
