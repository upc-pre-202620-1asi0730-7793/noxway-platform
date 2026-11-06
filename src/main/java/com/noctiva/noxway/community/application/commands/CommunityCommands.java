package com.noctiva.noxway.community.application.commands;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.*;

public final class CommunityCommands {
  public record Hours(
      @Min(1) @Max(7) int dayOfWeek,
      @NotBlank String opensAt,
      @NotBlank String closesAt,
      boolean closesNextDay,
      boolean is24Hours) {}

  public record Service(
      @NotBlank @Size(max = 150) String name,
      @Pattern(regexp = "PHARMACY|GAS_STATION|HOSPITAL|POLICE_STATION|OTHER") @NotNull String type,
      double latitude,
      double longitude,
      @NotBlank String timeZone,
      @NotEmpty @Size(max = 28) List<@Valid Hours> openingHours) {}

  public record Risk(
      @NotBlank @Size(max = 1000) String description,
      @Pattern(regexp = "LOW|MEDIUM|HIGH") @NotNull String riskLevel,
      double latitude,
      double longitude) {}

  public record Rating(
      @NotNull UUID commuteId,
      @Min(1) @Max(5) int score,
      @NotNull @Size(max = 1000) String comments) {}

  public record Post(
      @NotBlank @Size(max = 150) String title,
      @NotBlank @Size(max = 2000) String content,
      @NotBlank @Size(max = 100) String district) {}

  public record Vote(@Min(-1) @Max(1) int direction) {}
}
