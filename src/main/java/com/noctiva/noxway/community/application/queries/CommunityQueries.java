package com.noctiva.noxway.community.application.queries;

import java.time.Instant;
import java.util.UUID;

public final class CommunityQueries {
  private CommunityQueries() {}

  public record GetServicePointsQuery(
      UUID viewerId,
      String type,
      Instant at,
      boolean openOnly,
      Double latitude,
      Double longitude,
      Double radiusKm,
      int page,
      int size) {}

  public record GetMyServicePointsQuery(UUID reporterId) {}

  public record GetServicePointByIdQuery(UUID serviceId, UUID viewerId, boolean privileged, Instant at) {}

  public record GetApprovedRiskZonesQuery(int page, int size) {}

  public record GetMyRouteRatingsQuery(UUID workerId) {}

  public record GetCommunityPostsQuery(String district, int page, int size) {}
}
