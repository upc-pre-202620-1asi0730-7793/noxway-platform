package com.noctiva.noxway.commuting.domain.model.aggregates;

import java.time.Instant;
import java.util.UUID;

public record Commute(
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
  public static Commute create(
      UUID workerId,
      String origin,
      String destination,
      String transport,
      Instant startTime,
      Instant estimatedArrivalTime,
      int toleranceMinutes,
      String status,
      Instant arrivalTime,
      Instant delayUntil) {
    return new Commute(
        UUID.randomUUID(),
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
        Instant.now(),
        0);
  }

  public Commute withWorkerId(UUID value) {
    return new Commute(
        id,
        value,
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

  public Commute withOrigin(String value) {
    return new Commute(
        id,
        workerId,
        value,
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

  public Commute withDestination(String value) {
    return new Commute(
        id,
        workerId,
        origin,
        value,
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

  public Commute withTransport(String value) {
    return new Commute(
        id,
        workerId,
        origin,
        destination,
        value,
        startTime,
        estimatedArrivalTime,
        toleranceMinutes,
        status,
        arrivalTime,
        delayUntil,
        createdAt,
        version);
  }

  public Commute withStartTime(Instant value) {
    return new Commute(
        id,
        workerId,
        origin,
        destination,
        transport,
        value,
        estimatedArrivalTime,
        toleranceMinutes,
        status,
        arrivalTime,
        delayUntil,
        createdAt,
        version);
  }

  public Commute withEstimatedArrivalTime(Instant value) {
    return new Commute(
        id,
        workerId,
        origin,
        destination,
        transport,
        startTime,
        value,
        toleranceMinutes,
        status,
        arrivalTime,
        delayUntil,
        createdAt,
        version);
  }

  public Commute withToleranceMinutes(int value) {
    return new Commute(
        id,
        workerId,
        origin,
        destination,
        transport,
        startTime,
        estimatedArrivalTime,
        value,
        status,
        arrivalTime,
        delayUntil,
        createdAt,
        version);
  }

  public Commute withStatus(String value) {
    return new Commute(
        id,
        workerId,
        origin,
        destination,
        transport,
        startTime,
        estimatedArrivalTime,
        toleranceMinutes,
        value,
        arrivalTime,
        delayUntil,
        createdAt,
        version);
  }

  public Commute withArrivalTime(Instant value) {
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
        value,
        delayUntil,
        createdAt,
        version);
  }

  public Commute withDelayUntil(Instant value) {
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
        value,
        createdAt,
        version);
  }
}
