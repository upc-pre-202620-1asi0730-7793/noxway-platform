package com.noctiva.noxway.identity.application.commands;

import jakarta.validation.constraints.*;

public final class IdentityCommands {
  public record Register(
      @Email @NotBlank @Size(max = 254) String email,
      @NotBlank @Size(min = 12, max = 72) String password,
      @NotBlank @Size(max = 150) String fullName,
      @NotBlank @Size(max = 30) String phone,
      @Pattern(regexp = "WORKER|TRUSTED_CONTACT") @NotNull String role,
      String shiftType,
      String shiftStart,
      String shiftEnd,
      String timeZone) {}

  public record Login(
      @Email @NotBlank @Size(max = 254) String email, @NotBlank @Size(max = 72) String password) {}

  public record Profile(
      @NotBlank @Size(max = 150) String fullName,
      @NotBlank @Size(max = 30) String phone,
      String shiftType,
      String shiftStart,
      String shiftEnd,
      @NotBlank String timeZone) {}

  public record InviteTrustedContact(@Email @NotBlank String contactEmail) {}

  public record Preference(
      @Pattern(regexp = "EMAIL|WHATSAPP|SMS|IN_APP") @NotNull String channel,
      boolean enabled,
      @NotBlank String priorityStart,
      @NotBlank String priorityEnd,
      @NotBlank String timeZone) {}

  public record Password(
      @NotBlank String currentPassword, @Size(min = 12, max = 72) @NotNull String newPassword) {}
}
