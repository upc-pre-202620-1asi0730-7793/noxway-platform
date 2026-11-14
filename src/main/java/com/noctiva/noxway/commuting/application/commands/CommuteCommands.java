package com.noctiva.noxway.commuting.application.commands;

import java.time.Instant;
import java.util.UUID;

public final class CommuteCommands {
  private CommuteCommands() {}

  public record StartCommuteCommand(
      UUID workerId,
      String origin,
      String destination,
      String transport,
      Instant startTime,
      Instant estimatedArrivalTime,
      int toleranceMinutes) {}

  public record RecordTelemetryPingCommand(
      UUID commuteId,
      UUID actorId,
      double latitude,
      double longitude,
      double speed,
      Instant recordedAt) {}

  public record DelayCommuteCommand(UUID commuteId, UUID actorId, int minutes) {}

  public record ArriveCommuteCommand(UUID commuteId, UUID actorId) {}

  public record ReportSafetyIncidentCommand(
      UUID commuteId, UUID actorId, String type, String description) {}

  public record ResolveSafetyIncidentCommand(
      UUID incidentId, UUID actorId, boolean privileged) {}
}
