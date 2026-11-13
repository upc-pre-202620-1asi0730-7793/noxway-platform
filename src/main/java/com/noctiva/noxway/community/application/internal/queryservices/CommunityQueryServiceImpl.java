package com.noctiva.noxway.community.application.internal.queryservices;

import static com.noctiva.noxway.shared.domain.Rules.coordinates;
import static com.noctiva.noxway.shared.domain.Rules.require;

import com.noctiva.noxway.community.application.queries.CommunityQueries.*;
import com.noctiva.noxway.community.application.queryservices.CommunityQueryService;
import com.noctiva.noxway.community.domain.model.aggregates.*;
import com.noctiva.noxway.community.domain.model.valueobjects.OpeningHours;
import com.noctiva.noxway.community.domain.repositories.*;
import com.noctiva.noxway.profiles.interfaces.acl.ProfileDirectoryFacade;
import com.noctiva.noxway.profiles.interfaces.acl.ProfileDirectoryFacade.AccountSummary;
import com.noctiva.noxway.shared.application.result.ApplicationError;
import com.noctiva.noxway.shared.application.result.Result;
import com.noctiva.noxway.shared.domain.RuleViolation;
import com.noctiva.noxway.shared.interfaces.rest.Pages;
import com.noctiva.noxway.shared.interfaces.rest.resources.PagedResource;
import java.time.Clock;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class CommunityQueryServiceImpl implements CommunityQueryService {

  private final ServicePointRepository serviceRepository;
  private final ServiceOpeningIntervalRepository intervalRepository;
  private final ServiceVoteRepository voteRepository;
  private final RiskZoneRepository riskRepository;
  private final RouteRatingRepository ratingRepository;
  private final CommunityPostRepository postRepository;
  private final ProfileDirectoryFacade profileDirectory;
  private final Clock clock;

  public CommunityQueryServiceImpl(
      ServicePointRepository serviceRepository,
      ServiceOpeningIntervalRepository intervalRepository,
      ServiceVoteRepository voteRepository,
      RiskZoneRepository riskRepository,
      RouteRatingRepository ratingRepository,
      CommunityPostRepository postRepository,
      ProfileDirectoryFacade profileDirectory,
      Clock clock) {
    this.serviceRepository = serviceRepository;
    this.intervalRepository = intervalRepository;
    this.voteRepository = voteRepository;
    this.riskRepository = riskRepository;
    this.ratingRepository = ratingRepository;
    this.postRepository = postRepository;
    this.profileDirectory = profileDirectory;
    this.clock = clock;
  }

  @Override
  public Result<PagedResource<ServicePointView>, ApplicationError> handle(
      GetServicePointsQuery query) {
    Instant reference = query.at() == null ? clock.instant() : query.at();
    if (query.latitude() != null || query.longitude() != null || query.radiusKm() != null) {
      try {
        require(
            query.latitude() != null && query.longitude() != null && query.radiusKm() != null,
            "Latitude, longitude and radiusKm must be supplied together");
        coordinates(query.latitude(), query.longitude());
        require(query.radiusKm() > 0 && query.radiusKm() <= 100, "Radius must be 0..100 km");
      } catch (RuleViolation ex) {
        return Result.failure(new ApplicationError.ValidationError(ex.getMessage()));
      }
    }

    var filtered =
        serviceRepository.findByStatus("APPROVED").stream()
            .filter(s -> query.type() == null || query.type().isBlank() || s.getType().equals(query.type()))
            .filter(
                s ->
                    query.latitude() == null
                        || OpeningHours.distanceKm(
                                query.latitude(),
                                query.longitude(),
                                s.getLatitude(),
                                s.getLongitude())
                            <= query.radiusKm())
            .map(s -> toView(s, reference, query.viewerId()))
            .filter(v -> !query.openOnly() || v.openAtReference())
            .sorted(Comparator.comparingInt(ServicePointView::trustScore).reversed())
            .toList();

    return Result.success(paginate(filtered, query.page(), query.size()));
  }

  @Override
  public Result<List<ServicePointView>, ApplicationError> handle(GetMyServicePointsQuery query) {
    Instant now = clock.instant();
    var list =
        serviceRepository.findByReportedBy(query.reporterId()).stream()
            .sorted(Comparator.comparing(ServicePoint::getCreatedAt).reversed())
            .map(s -> toView(s, now, query.reporterId()))
            .toList();
    return Result.success(list);
  }

  @Override
  public Result<ServicePointView, ApplicationError> handle(GetServicePointByIdQuery query) {
    var serviceOpt = serviceRepository.findById(query.serviceId());
    if (serviceOpt.isEmpty()) {
      return Result.failure(
          new ApplicationError.NotFoundError(
              "community.service.not_found", "Service point not found"));
    }
    var service = serviceOpt.get();
    if (!"APPROVED".equals(service.getStatus())
        && !service.getReportedBy().equals(query.viewerId())
        && !query.privileged()) {
      return Result.failure(
          new ApplicationError.NotFoundError(
              "community.service.not_published", "Service point is not published"));
    }
    Instant ref = query.at() == null ? clock.instant() : query.at();
    return Result.success(toView(service, ref, query.viewerId()));
  }

  @Override
  public Result<PagedResource<RiskZoneView>, ApplicationError> handle(
      GetApprovedRiskZonesQuery query) {
    var views =
        riskRepository.findByStatus("APPROVED").stream()
            .sorted(Comparator.comparing(RiskZone::getCreatedAt).reversed())
            .map(
                r -> {
                  String reporter =
                      profileDirectory
                          .findById(r.getReportedBy())
                          .map(AccountSummary::fullName)
                          .orElse("");
                  return new RiskZoneView(
                      r.getId(),
                      r.getReportedBy(),
                      reporter,
                      r.getDescription(),
                      r.getRiskLevel(),
                      r.getLatitude(),
                      r.getLongitude(),
                      r.getStatus(),
                      r.getCreatedAt());
                })
            .toList();
    return Result.success(paginate(views, query.page(), query.size()));
  }

  @Override
  public Result<List<RouteRating>, ApplicationError> handle(GetMyRouteRatingsQuery query) {
    var list =
        ratingRepository.findByWorkerId(query.workerId()).stream()
            .sorted(Comparator.comparing(RouteRating::getCreatedAt).reversed())
            .toList();
    return Result.success(list);
  }

  @Override
  public Result<PagedResource<CommunityPostView>, ApplicationError> handle(
      GetCommunityPostsQuery query) {
    var list =
        postRepository.findAll().stream()
            .filter(
                p ->
                    query.district() == null
                        || query.district().isBlank()
                        || p.getDistrict().equalsIgnoreCase(query.district().trim()))
            .sorted(Comparator.comparing(CommunityPost::getCreatedAt).reversed())
            .map(
                p -> {
                  var profile = profileDirectory.findById(p.getAuthorId()).orElse(null);
                  return new CommunityPostView(
                      p.getId(),
                      p.getAuthorId(),
                      profile != null ? profile.fullName() : "",
                      profile != null ? profile.role() : "WORKER",
                      p.getTitle(),
                      p.getContent(),
                      p.getDistrict(),
                      p.getCategory(),
                      p.getLatitude(),
                      p.getLongitude(),
                      p.getCreatedAt());
                })
            .toList();
    return Result.success(paginate(list, query.page(), query.size()));
  }

  private ServicePointView toView(ServicePoint s, Instant at, UUID viewerId) {
    var hours = intervalRepository.findByServiceId(s.getId());
    var votes = voteRepository.findByServiceId(s.getId());
    int score = votes.stream().mapToInt(ServiceVote::getDirection).sum();
    long up = votes.stream().filter(v -> v.getDirection() > 0).count();
    long down = votes.stream().filter(v -> v.getDirection() < 0).count();
    int myVote =
        viewerId == null
            ? 0
            : votes.stream()
                .filter(v -> v.getAccountId().equals(viewerId))
                .mapToInt(ServiceVote::getDirection)
                .findFirst()
                .orElse(0);
    String reporter =
        profileDirectory.findById(s.getReportedBy()).map(AccountSummary::fullName).orElse("");
    return new ServicePointView(
        s.getId(),
        s.getReportedBy(),
        reporter,
        s.getName(),
        s.getType(),
        s.getLatitude(),
        s.getLongitude(),
        s.getTimeZone(),
        s.getStatus(),
        hours,
        score,
        up,
        down,
        myVote,
        OpeningHours.isOpen(hours, s.getTimeZone(), at),
        at,
        s.getCreatedAt());
  }

  private static <T> PagedResource<T> paginate(List<T> items, int page, int size) {
    int clampedPage = Math.max(0, page);
    int clampedSize = Pages.clamp(size);
    int from = Math.min(clampedPage * clampedSize, items.size());
    int to = Math.min(from + clampedSize, items.size());
    return new PagedResource<>(items.subList(from, to), clampedPage, clampedSize, items.size());
  }
}
