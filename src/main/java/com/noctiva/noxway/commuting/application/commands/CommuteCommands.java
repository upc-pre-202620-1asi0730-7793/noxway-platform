package com.noctiva.noxway.commuting.application.commands;

import jakarta.validation.constraints.*;
import java.time.Instant;

public final class CommuteCommands {
  public record Start(
      @NotBlank @Size(max = 500) String origin,
      @NotBlank @Size(max = 500) String destination,
      @NotBlank @Size(max = 50) String transport,
      @NotNull Instant startTime,
      @NotNull Instant estimatedArrivalTime,
      @Min(0) @Max(120) int toleranceMinutes) {}

  public record Ping(
      @DecimalMin("-90") @DecimalMax("90") double latitude,
      @DecimalMin("-180") @DecimalMax("180") double longitude,
      @Min(0) @Max(300) double speed,
      @NotNull Instant recordedAt) {}

  public record Delay(@Min(1) @Max(120) int minutes) {}

  public record Incident(
      @Pattern(regexp = "SOS|DELAY|DEVIATION|OTHER") @NotNull String type,
      @NotBlank @Size(max = 1000) String description) {}
}
