package com.noctiva.noxway.wellness.application.commands;

import jakarta.validation.constraints.*;
import java.time.Instant;

public final class WellnessCommands {
  public record Rest(
      @NotNull Instant startTime,
      @NotNull Instant endTime,
      @Min(1) @Max(5) int quality,
      @NotNull @Size(max = 1000) String notes) {}

  public record Goal(@Min(60) @Max(1440) int targetMinutes) {}
}
