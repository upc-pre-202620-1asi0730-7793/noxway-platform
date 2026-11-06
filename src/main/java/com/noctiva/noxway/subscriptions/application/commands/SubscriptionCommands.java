package com.noctiva.noxway.subscriptions.application.commands;

import jakarta.validation.constraints.*;
import java.util.UUID;

public final class SubscriptionCommands {
  public record Subscribe(@NotNull UUID planId) {}

  public record Payment(@NotBlank @Size(max = 150) String reference) {}

  public record Invite(@Email @NotBlank String workerEmail) {}

  public record Referral(@NotBlank String code) {}

  public record Benefit(
      @NotBlank @Size(max = 150) String name,
      @NotBlank @Size(max = 1500) String description,
      @NotBlank String planCode,
      boolean active) {}
}
