package com.noctiva.noxway.moderation.application.commands;

import java.util.UUID;

public final class ModerationCommands {
  private ModerationCommands() {}

  public record SubmitCommunityReportCommand(
      UUID reportedBy, String targetType, UUID targetId, String reason) {}

  public record DecideReportCommand(
      UUID reportId, UUID moderatorId, String decision, String notes) {}
}
