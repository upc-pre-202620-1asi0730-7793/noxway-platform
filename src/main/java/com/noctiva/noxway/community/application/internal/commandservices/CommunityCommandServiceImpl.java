package com.noctiva.noxway.community.application.internal.commandservices;

import com.noctiva.noxway.community.application.commands.CommunityCommands.*;
import com.noctiva.noxway.community.application.commandservices.CommunityCommandService;
import com.noctiva.noxway.community.application.queryservices.CommunityQueryService.ServicePointView;
import com.noctiva.noxway.community.domain.model.aggregates.*;
import com.noctiva.noxway.community.domain.model.valueobjects.OpeningHours;
import com.noctiva.noxway.community.domain.repositories.*;
import com.noctiva.noxway.commuting.interfaces.acl.CommuteAccess;
import com.noctiva.noxway.moderation.interfaces.acl.ModerationQueue;
import com.noctiva.noxway.profiles.interfaces.acl.ProfileDirectoryFacade;
import com.noctiva.noxway.profiles.interfaces.acl.ProfileDirectoryFacade.AccountSummary;
import com.noctiva.noxway.shared.application.result.ApplicationError;
import com.noctiva.noxway.shared.application.result.Result;
import com.noctiva.noxway.shared.domain.RuleViolation;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CommunityCommandServiceImpl implements CommunityCommandService {

  private final ServicePointRepository serviceRepository;
  private final ServiceOpeningIntervalRepository intervalRepository;
  private final ServiceVoteRepository voteRepository;
  private final RiskZoneRepository riskRepository;
  private final RouteRatingRepository ratingRepository;
  private final CommunityPostRepository postRepository;
  private final ModerationQueue moderationQueue;
  private final CommuteAccess commuteAccess;
  private final ProfileDirectoryFacade profileDirectory;
  private final Clock clock;

  public CommunityCommandServiceImpl(
      ServicePointRepository serviceRepository,
      ServiceOpeningIntervalRepository intervalRepository,
      ServiceVoteRepository voteRepository,
      RiskZoneRepository riskRepository,
      RouteRatingRepository ratingRepository,
      CommunityPostRepository postRepository,
      ModerationQueue moderationQueue,
      CommuteAccess commuteAccess,
      ProfileDirectoryFacade profileDirectory,
      Clock clock) {
    this.serviceRepository = serviceRepository;
    this.intervalRepository = intervalRepository;
    this.voteRepository = voteRepository;
    this.riskRepository = riskRepository;
    this.ratingRepository = ratingRepository;
    this.postRepository = postRepository;
    this.moderationQueue = moderationQueue;
    this.commuteAccess = commuteAccess;
    this.profileDirectory = profileDirectory;
    this.clock = clock;
  }

  @Override
  public Result<ServicePointView, ApplicationError> handle(SubmitServicePointCommand cmd) {
    try {
      var service =
          ServicePoint.create(
              cmd.reportedBy(),
              cmd.name(),
              cmd.type(),
              cmd.latitude(),
              cmd.longitude(),
              cmd.timeZone(),
              "PENDING");
      List<ServiceOpeningInterval> createdHours =
          cmd.openingHours().stream()
              .map(
                  h ->
                      ServiceOpeningInterval.create(
                          service.getId(),
                          h.dayOfWeek(),
                          h.opensAt(),
                          h.closesAt(),
                          h.closesNextDay(),
                          h.is24Hours()))
              .toList();
      var saved = serviceRepository.save(service);
      var savedHours = createdHours.stream().map(intervalRepository::save).toList();
      moderationQueue.submit(
          cmd.reportedBy(), "SERVICE", saved.getId(), "New service and opening hours");
      return Result.success(toView(saved, savedHours, cmd.reportedBy()));
    } catch (RuleViolation ex) {
      return Result.failure(new ApplicationError.BusinessRuleError(ex.getMessage()));
    }
  }

  @Override
  public Result<ServicePointView, ApplicationError> handle(UpdateServicePointCommand cmd) {
    var existingOpt = serviceRepository.findById(cmd.serviceId());
    if (existingOpt.isEmpty()) {
      return Result.failure(
          new ApplicationError.NotFoundError(
              "community.service.not_found", "Service point not found"));
    }
    var service = existingOpt.get();
    if (!service.getReportedBy().equals(cmd.actorId())) {
      return Result.failure(
          new ApplicationError.ForbiddenError("Only the service reporter can edit it"));
    }
    try {
      List<ServiceOpeningInterval> nextHours =
          cmd.openingHours().stream()
              .map(
                  h ->
                      ServiceOpeningInterval.create(
                          service.getId(),
                          h.dayOfWeek(),
                          h.opensAt(),
                          h.closesAt(),
                          h.closesNextDay(),
                          h.is24Hours()))
              .toList();
      service.updateForReview(
          cmd.name(), cmd.type(), cmd.latitude(), cmd.longitude(), cmd.timeZone());
      var saved = serviceRepository.save(service);
      intervalRepository
          .findByServiceId(service.getId())
          .forEach(h -> intervalRepository.delete(h.getId()));
      var savedHours = nextHours.stream().map(intervalRepository::save).toList();
      moderationQueue.submit(
          cmd.actorId(),
          "SERVICE",
          saved.getId(),
          "Service information or opening hours changed");
      return Result.success(toView(saved, savedHours, cmd.actorId()));
    } catch (RuleViolation ex) {
      return Result.failure(new ApplicationError.BusinessRuleError(ex.getMessage()));
    }
  }

  @Override
  public Result<ServiceVote, ApplicationError> handle(VoteServicePointCommand cmd) {
    var serviceOpt = serviceRepository.findById(cmd.serviceId());
    if (serviceOpt.isEmpty()) {
      return Result.failure(
          new ApplicationError.NotFoundError(
              "community.service.not_found", "Service point not found"));
    }
    var service = serviceOpt.get();
    if (!"APPROVED".equals(service.getStatus())) {
      return Result.failure(
          new ApplicationError.BusinessRuleError(
              "community.service.not_published", "Only approved services can receive votes"));
    }
    try {
      var existing =
          voteRepository.findByServiceId(cmd.serviceId()).stream()
              .filter(v -> v.getAccountId().equals(cmd.accountId()))
              .findFirst();
      if (cmd.direction() == 0) {
        UUID voteId = existing.map(ServiceVote::getId).orElseGet(UUID::randomUUID);
        Instant createdAt = existing.map(ServiceVote::getCreatedAt).orElseGet(clock::instant);
        existing.ifPresent(v -> voteRepository.delete(v.getId()));
        return Result.success(
            ServiceVote.reconstitute(voteId, cmd.serviceId(), cmd.accountId(), 0, createdAt, 0L));
      }
      ServiceVote vote;
      if (existing.isPresent()) {
        vote = existing.get();
        vote.changeDirection(cmd.direction());
      } else {
        vote = ServiceVote.create(cmd.serviceId(), cmd.accountId(), cmd.direction());
      }
      return Result.success(voteRepository.save(vote));
    } catch (RuleViolation ex) {
      return Result.failure(new ApplicationError.BusinessRuleError(ex.getMessage()));
    }
  }

  @Override
  public Result<RiskZone, ApplicationError> handle(ReportRiskZoneCommand cmd) {
    try {
      var risk =
          riskRepository.save(
              RiskZone.create(
                  cmd.reportedBy(),
                  cmd.description(),
                  cmd.riskLevel(),
                  cmd.latitude(),
                  cmd.longitude(),
                  "PENDING"));
      moderationQueue.submit(cmd.reportedBy(), "RISK", risk.getId(), "New risk-zone report");
      return Result.success(risk);
    } catch (RuleViolation ex) {
      return Result.failure(new ApplicationError.BusinessRuleError(ex.getMessage()));
    }
  }

  @Override
  public Result<RiskZone, ApplicationError> handle(ResolveRiskZoneCommand cmd) {
    var riskOpt = riskRepository.findById(cmd.riskId());
    if (riskOpt.isEmpty()) {
      return Result.failure(
          new ApplicationError.NotFoundError("community.risk.not_found", "Risk zone not found"));
    }
    try {
      var risk = riskOpt.get();
      risk.resolve();
      return Result.success(riskRepository.save(risk));
    } catch (RuleViolation ex) {
      return Result.failure(new ApplicationError.BusinessRuleError(ex.getMessage()));
    }
  }

  @Override
  public Result<RouteRating, ApplicationError> handle(RateRouteCommand cmd) {
    var accessCheck = commuteAccess.verifyCompletedOwn(cmd.commuteId(), cmd.workerId());
    if (accessCheck.isFailure()) {
      return Result.failure(accessCheck.error());
    }
    boolean alreadyRated =
        ratingRepository.findByCommuteId(cmd.commuteId()).stream()
            .anyMatch(r -> r.getWorkerId().equals(cmd.workerId()));
    if (alreadyRated) {
      return Result.failure(
          new ApplicationError.ConflictError(
              "community.rating.already_exists", "Commute has already been rated"));
    }
    try {
      return Result.success(
          ratingRepository.save(
              RouteRating.create(cmd.commuteId(), cmd.workerId(), cmd.score(), cmd.comments())));
    } catch (RuleViolation ex) {
      return Result.failure(new ApplicationError.BusinessRuleError(ex.getMessage()));
    }
  }

  @Override
  public Result<CommunityPost, ApplicationError> handle(CreateCommunityPostCommand cmd) {
    try {
      var post =
          CommunityPost.create(
              cmd.authorId(),
              cmd.title(),
              cmd.content(),
              cmd.district(),
              cmd.category(),
              cmd.latitude(),
              cmd.longitude());
      return Result.success(postRepository.save(post));
    } catch (RuleViolation ex) {
      return Result.failure(new ApplicationError.BusinessRuleError(ex.getMessage()));
    }
  }

  @Override
  public Result<Void, ApplicationError> handle(DeleteCommunityPostCommand cmd) {
    var postOpt = postRepository.findById(cmd.postId());
    if (postOpt.isEmpty()) {
      return Result.failure(
          new ApplicationError.NotFoundError("community.post.not_found", "Community post not found"));
    }
    var post = postOpt.get();
    if (!cmd.privileged() && !post.getAuthorId().equals(cmd.actorId())) {
      return Result.failure(
          new ApplicationError.ForbiddenError("Only the post author or staff can delete it"));
    }
    postRepository.delete(post.getId());
    return Result.success(null);
  }

  private ServicePointView toView(
      ServicePoint s, List<ServiceOpeningInterval> hours, UUID viewerId) {
    Instant now = clock.instant();
    var votes = voteRepository.findByServiceId(s.getId());
    int score = votes.stream().mapToInt(ServiceVote::getDirection).sum();
    long up = votes.stream().filter(v -> v.getDirection() > 0).count();
    long down = votes.stream().filter(v -> v.getDirection() < 0).count();
    int myVote =
        votes.stream()
            .filter(v -> v.getAccountId().equals(viewerId))
            .mapToInt(ServiceVote::getDirection)
            .findFirst()
            .orElse(0);
    String reporterName =
        profileDirectory
            .findById(s.getReportedBy())
            .map(AccountSummary::fullName)
            .orElse("");
    return new ServicePointView(
        s.getId(),
        s.getReportedBy(),
        reporterName,
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
        OpeningHours.isOpen(hours, s.getTimeZone(), now),
        now,
        s.getCreatedAt());
  }
}
