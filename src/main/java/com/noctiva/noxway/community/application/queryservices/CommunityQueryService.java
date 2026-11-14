package com.noctiva.noxway.community.application.queryservices;

import com.noctiva.noxway.community.application.queries.CommunityQueries.*;
import com.noctiva.noxway.community.domain.model.aggregates.RouteRating;
import com.noctiva.noxway.community.domain.model.aggregates.ServiceOpeningInterval;
import com.noctiva.noxway.shared.application.result.ApplicationError;
import com.noctiva.noxway.shared.application.result.Result;
import com.noctiva.noxway.shared.interfaces.rest.resources.PagedResource;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface CommunityQueryService {

  Result<PagedResource<ServicePointView>, ApplicationError> handle(GetServicePointsQuery query);

  Result<List<ServicePointView>, ApplicationError> handle(GetMyServicePointsQuery query);

  Result<ServicePointView, ApplicationError> handle(GetServicePointByIdQuery query);

  Result<PagedResource<RiskZoneView>, ApplicationError> handle(GetApprovedRiskZonesQuery query);

  Result<List<RouteRating>, ApplicationError> handle(GetMyRouteRatingsQuery query);

  Result<PagedResource<CommunityPostView>, ApplicationError> handle(GetCommunityPostsQuery query);

  record ServicePointView(
      UUID id,
      UUID reportedBy,
      String reporterName,
      String name,
      String type,
      double latitude,
      double longitude,
      String timeZone,
      String status,
      List<ServiceOpeningInterval> openingHours,
      int trustScore,
      long upvotes,
      long downvotes,
      int myVote,
      boolean openAtReference,
      Instant referenceTime,
      Instant createdAt) {}

  record RiskZoneView(
      UUID id,
      UUID reportedBy,
      String reporterName,
      String description,
      String riskLevel,
      double latitude,
      double longitude,
      String status,
      Instant createdAt) {}

  record CommunityPostView(
      UUID id,
      UUID authorId,
      String authorName,
      String authorRole,
      String title,
      String content,
      String district,
      String category,
      Double latitude,
      Double longitude,
      Instant createdAt) {}
}
