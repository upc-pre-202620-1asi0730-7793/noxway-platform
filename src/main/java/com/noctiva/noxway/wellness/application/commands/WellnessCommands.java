package com.noctiva.noxway.wellness.application.commands;

import java.time.Instant;
import java.util.UUID;

public final class WellnessCommands {
  private WellnessCommands() {}

  public record LogRestSessionCommand(
      UUID workerId, Instant startTime, Instant endTime, int quality, String notes) {}

  public record DeleteRestSessionCommand(UUID sessionId, UUID actorId) {}

  public record UpdateSleepGoalCommand(UUID workerId, int targetMinutes) {}
}
