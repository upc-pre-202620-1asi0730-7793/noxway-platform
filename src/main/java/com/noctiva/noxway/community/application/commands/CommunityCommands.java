package com.noctiva.noxway.community.application.commands;

import java.util.List;
import java.util.UUID;

public final class CommunityCommands {
  private CommunityCommands() {}

  public record OpeningIntervalInput(
      int dayOfWeek, String opensAt, String closesAt, boolean closesNextDay, boolean is24Hours) {}

  public record SubmitServicePointCommand(
      UUID reportedBy,
      String name,
      String type,
      double latitude,
      double longitude,
      String timeZone,
      List<OpeningIntervalInput> openingHours) {}

  public record UpdateServicePointCommand(
      UUID serviceId,
      UUID actorId,
      String name,
      String type,
      double latitude,
      double longitude,
      String timeZone,
      List<OpeningIntervalInput> openingHours) {}

  public record VoteServicePointCommand(UUID serviceId, UUID accountId, int direction) {}

  public record ReportRiskZoneCommand(
      UUID reportedBy, String description, String riskLevel, double latitude, double longitude) {}

  public record ResolveRiskZoneCommand(UUID riskId, UUID actorId) {}

  public record RateRouteCommand(UUID commuteId, UUID workerId, int score, String comments) {}

  public record CreateCommunityPostCommand(
      UUID authorId, String title, String content, String district) {}

  public record DeleteCommunityPostCommand(UUID postId, UUID actorId, boolean privileged) {}
}
