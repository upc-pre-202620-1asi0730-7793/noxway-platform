package com.noctiva.noxway.community.interfaces.rest.transform;

import com.noctiva.noxway.community.application.commands.CommunityCommands.*;
import com.noctiva.noxway.community.application.queryservices.CommunityQueryService.*;
import com.noctiva.noxway.community.domain.model.aggregates.*;
import com.noctiva.noxway.community.interfaces.rest.resources.*;
import com.noctiva.noxway.profiles.interfaces.acl.ProfileDirectoryFacade.AccountSummary;
import com.noctiva.noxway.shared.interfaces.rest.resources.PagedResource;
import java.util.List;
import java.util.UUID;

public final class CommunityRestAssembler {
  private CommunityRestAssembler() {}

  public static SubmitServicePointCommand toCommand(UUID actorId, SubmitServicePointResource r) {
    var hours =
        r.openingHours().stream()
            .map(
                h ->
                    new OpeningIntervalInput(
                        h.dayOfWeek(), h.opensAt(), h.closesAt(), h.closesNextDay(), h.is24Hours()))
            .toList();
    return new SubmitServicePointCommand(
        actorId, r.name(), r.type(), r.latitude(), r.longitude(), r.timeZone(), hours);
  }

  public static UpdateServicePointCommand toCommand(
      UUID serviceId, UUID actorId, SubmitServicePointResource r) {
    var hours =
        r.openingHours().stream()
            .map(
                h ->
                    new OpeningIntervalInput(
                        h.dayOfWeek(), h.opensAt(), h.closesAt(), h.closesNextDay(), h.is24Hours()))
            .toList();
    return new UpdateServicePointCommand(
        serviceId, actorId, r.name(), r.type(), r.latitude(), r.longitude(), r.timeZone(), hours);
  }

  public static VoteServicePointCommand toCommand(
      UUID serviceId, UUID accountId, VoteServicePointResource r) {
    return new VoteServicePointCommand(serviceId, accountId, r.direction());
  }

  public static ReportRiskZoneCommand toCommand(UUID reporterId, ReportRiskZoneResource r) {
    return new ReportRiskZoneCommand(
        reporterId, r.description(), r.riskLevel(), r.latitude(), r.longitude());
  }

  public static RateRouteCommand toCommand(UUID workerId, RateRouteResource r) {
    return new RateRouteCommand(r.commuteId(), workerId, r.score(), r.comments());
  }

  public static CreateCommunityPostCommand toCommand(
      UUID authorId, CreateCommunityPostResource r) {
    return new CreateCommunityPostCommand(
        authorId, r.title(), r.content(), r.district(), r.category(), r.latitude(), r.longitude());
  }

  public static ServicePointResource toResource(ServicePointView v) {
    var hours =
        v.openingHours().stream()
            .map(
                h ->
                    new ServiceOpeningIntervalResource(
                        h.getDayOfWeek(),
                        h.getOpensAt(),
                        h.getClosesAt(),
                        h.isClosesNextDay(),
                        h.is24Hours()))
            .toList();
    return new ServicePointResource(
        v.id(),
        v.reportedBy(),
        v.reporterName(),
        v.name(),
        v.type(),
        v.latitude(),
        v.longitude(),
        v.timeZone(),
        v.status(),
        hours,
        v.trustScore(),
        v.upvotes(),
        v.downvotes(),
        v.myVote(),
        v.openAtReference(),
        v.referenceTime(),
        v.createdAt());
  }

  public static List<ServicePointResource> toServiceResources(List<ServicePointView> list) {
    return list.stream().map(CommunityRestAssembler::toResource).toList();
  }

  public static PagedResource<ServicePointResource> toPagedServices(
      PagedResource<ServicePointView> page) {
    return new PagedResource<>(
        toServiceResources(page.items()), page.page(), page.size(), page.total());
  }

  public static ServiceVoteResource toResource(ServiceVote v) {
    return new ServiceVoteResource(
        v.getId(), v.getServiceId(), v.getAccountId(), v.getDirection(), v.getCreatedAt());
  }

  public static RiskZoneResource toResource(RiskZone r) {
    return toResource(r, null);
  }

  public static RiskZoneResource toResource(RiskZone r, AccountSummary reporter) {
    return new RiskZoneResource(
        r.getId(),
        r.getReportedBy(),
        reporter != null ? reporter.fullName() : "",
        r.getDescription(),
        r.getRiskLevel(),
        r.getLatitude(),
        r.getLongitude(),
        r.getStatus(),
        r.getCreatedAt());
  }

  public static PagedResource<RiskZoneResource> toPagedRisks(PagedResource<RiskZoneView> page) {
    return new PagedResource<>(
        page.items().stream()
            .map(
                v ->
                    new RiskZoneResource(
                        v.id(),
                        v.reportedBy(),
                        v.reporterName(),
                        v.description(),
                        v.riskLevel(),
                        v.latitude(),
                        v.longitude(),
                        v.status(),
                        v.createdAt()))
            .toList(),
        page.page(),
        page.size(),
        page.total());
  }

  public static RouteRatingResource toResource(RouteRating r) {
    return new RouteRatingResource(
        r.getId(),
        r.getCommuteId(),
        r.getWorkerId(),
        r.getScore(),
        r.getComments(),
        r.getCreatedAt());
  }

  public static List<RouteRatingResource> toRatingResources(List<RouteRating> list) {
    return list.stream().map(CommunityRestAssembler::toResource).toList();
  }

  public static CommunityPostResource toResource(CommunityPost p) {
    return toResource(p, null);
  }

  public static CommunityPostResource toResource(CommunityPost p, AccountSummary author) {
    return new CommunityPostResource(
        p.getId(),
        p.getAuthorId(),
        author != null ? author.fullName() : "",
        author != null ? author.role() : "WORKER",
        p.getTitle(),
        p.getContent(),
        p.getDistrict(),
        p.getCategory(),
        p.getLatitude(),
        p.getLongitude(),
        p.getCreatedAt());
  }

  public static PagedResource<CommunityPostResource> toPagedPosts(
      PagedResource<CommunityPostView> page) {
    return new PagedResource<>(
        page.items().stream()
            .map(
                v ->
                    new CommunityPostResource(
                        v.id(),
                        v.authorId(),
                        v.authorName(),
                        v.authorRole(),
                        v.title(),
                        v.content(),
                        v.district(),
                        v.category(),
                        v.latitude(),
                        v.longitude(),
                        v.createdAt()))
            .toList(),
        page.page(),
        page.size(),
        page.total());
  }
}
