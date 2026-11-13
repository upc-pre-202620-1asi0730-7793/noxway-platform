package com.noctiva.noxway.profiles.application.commands;

import java.util.UUID;

public final class ProfileCommands {
  private ProfileCommands() {}

  public record CreateProfileCommand(
      String email,
      String fullName,
      String phone,
      String role,
      String shiftType,
      String shiftStart,
      String shiftEnd,
      String timeZone) {}

  public record UpdateProfileCommand(
      UUID accountId,
      String fullName,
      String phone,
      String shiftType,
      String shiftStart,
      String shiftEnd,
      String timeZone) {}

  public record UpdateNotificationPreferenceCommand(
      UUID accountId,
      String channel,
      boolean enabled,
      String priorityStart,
      String priorityEnd,
      String timeZone) {}

  public record CreateTrustLinkCommand(UUID workerId, String contactEmail) {}

  public record AcceptTrustLinkCommand(UUID linkId, UUID actorId) {}

  public record RejectTrustLinkCommand(UUID linkId, UUID actorId) {}

  public record RevokeTrustLinkCommand(UUID linkId, UUID actorId) {}
}
