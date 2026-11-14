package com.noctiva.noxway.commuting.application.internal.commandservices;

import com.noctiva.noxway.commuting.application.commands.CommuteCommands.*;
import com.noctiva.noxway.commuting.application.commandservices.CommuteCommandService;
import com.noctiva.noxway.commuting.application.internal.eventhandlers.CommuteEventHandler;
import com.noctiva.noxway.commuting.domain.model.aggregates.*;
import com.noctiva.noxway.commuting.domain.repositories.*;
import com.noctiva.noxway.profiles.interfaces.acl.ProfileDirectoryFacade;
import com.noctiva.noxway.shared.application.result.ApplicationError;
import com.noctiva.noxway.shared.application.result.Result;
import com.noctiva.noxway.shared.domain.RuleViolation;
import java.time.Clock;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CommuteCommandServiceImpl implements CommuteCommandService {

  private final CommuteRepository commuteRepository;
  private final TelemetryPingRepository telemetryRepository;
  private final SafetyIncidentRepository incidentRepository;
  private final CommuteEventHandler eventHandler;
  private final ProfileDirectoryFacade profileDirectory;
  private final Clock clock;

  public CommuteCommandServiceImpl(
      CommuteRepository commuteRepository,
      TelemetryPingRepository telemetryRepository,
      SafetyIncidentRepository incidentRepository,
      CommuteEventHandler eventHandler,
      ProfileDirectoryFacade profileDirectory,
      Clock clock) {
    this.commuteRepository = commuteRepository;
    this.telemetryRepository = telemetryRepository;
    this.incidentRepository = incidentRepository;
    this.eventHandler = eventHandler;
    this.profileDirectory = profileDirectory;
    this.clock = clock;
  }

  @Override
  public Result<Commute, ApplicationError> handle(StartCommuteCommand cmd) {
    boolean alreadyRunning =
        commuteRepository.findByWorkerId(cmd.workerId()).stream().anyMatch(Commute::isOngoing);
    if (alreadyRunning) {
      return Result.failure(
          new ApplicationError.ConflictError(
              "commuting.already_active", "Worker already has an active night commute"));
    }
    try {
      var commute =
          Commute.start(
              cmd.workerId(),
              cmd.origin(),
              cmd.destination(),
              cmd.transport(),
              cmd.startTime(),
              cmd.estimatedArrivalTime(),
              cmd.toleranceMinutes());
      return Result.success(commuteRepository.save(commute));
    } catch (RuleViolation ex) {
      return Result.failure(new ApplicationError.BusinessRuleError(ex.getMessage()));
    }
  }

  @Override
  public Result<TelemetryPing, ApplicationError> handle(RecordTelemetryPingCommand cmd) {
    var commuteOpt = commuteRepository.findById(cmd.commuteId());
    if (commuteOpt.isEmpty()) {
      return Result.failure(
          new ApplicationError.NotFoundError("commuting.not_found", "Commute not found"));
    }
    var commute = commuteOpt.get();
    if (!commute.getWorkerId().equals(cmd.actorId())) {
      return Result.failure(
          new ApplicationError.ForbiddenError("Only commute owner can submit telemetry"));
    }
    if (!commute.isOngoing()) {
      return Result.failure(
          new ApplicationError.BusinessRuleError(
              "commuting.already_arrived", "Cannot record telemetry for an arrived commute"));
    }
    try {
      var ping =
          TelemetryPing.create(
              commute.getId(), cmd.latitude(), cmd.longitude(), cmd.speed(), cmd.recordedAt());
      return Result.success(telemetryRepository.save(ping));
    } catch (RuleViolation ex) {
      return Result.failure(new ApplicationError.BusinessRuleError(ex.getMessage()));
    }
  }

  @Override
  public Result<Commute, ApplicationError> handle(DelayCommuteCommand cmd) {
    var commuteOpt = commuteRepository.findById(cmd.commuteId());
    if (commuteOpt.isEmpty()) {
      return Result.failure(
          new ApplicationError.NotFoundError("commuting.not_found", "Commute not found"));
    }
    var commute = commuteOpt.get();
    if (!commute.getWorkerId().equals(cmd.actorId())) {
      return Result.failure(
          new ApplicationError.ForbiddenError("Only commute owner can delay commute"));
    }
    try {
      commute.extendDelay(cmd.minutes(), clock.instant());
      return Result.success(commuteRepository.save(commute));
    } catch (RuleViolation ex) {
      return Result.failure(new ApplicationError.BusinessRuleError(ex.getMessage()));
    }
  }

  @Override
  public Result<Commute, ApplicationError> handle(ArriveCommuteCommand cmd) {
    var commuteOpt = commuteRepository.findById(cmd.commuteId());
    if (commuteOpt.isEmpty()) {
      return Result.failure(
          new ApplicationError.NotFoundError("commuting.not_found", "Commute not found"));
    }
    var commute = commuteOpt.get();
    if (!commute.getWorkerId().equals(cmd.actorId())) {
      return Result.failure(
          new ApplicationError.ForbiddenError("Only commute owner can mark safe arrival"));
    }
    try {
      commute.arrive(clock.instant());
      return Result.success(commuteRepository.save(commute));
    } catch (RuleViolation ex) {
      return Result.failure(new ApplicationError.BusinessRuleError(ex.getMessage()));
    }
  }

  @Override
  public Result<SafetyIncident, ApplicationError> handle(ReportSafetyIncidentCommand cmd) {
    var commuteOpt = commuteRepository.findById(cmd.commuteId());
    if (commuteOpt.isEmpty()) {
      return Result.failure(
          new ApplicationError.NotFoundError("commuting.not_found", "Commute not found"));
    }
    var commute = commuteOpt.get();
    boolean authorized =
        commute.getWorkerId().equals(cmd.actorId())
            || profileDirectory.isActiveTrustLink(commute.getWorkerId(), cmd.actorId());
    if (!authorized) {
      return Result.failure(
          new ApplicationError.ForbiddenError(
              "Only the worker or an active trusted contact can report an incident"));
    }
    if (!commute.isOngoing()) {
      return Result.failure(
          new ApplicationError.BusinessRuleError(
              "commuting.already_arrived", "Cannot report incident on a completed commute"));
    }
    try {
      var incident =
          SafetyIncident.report(
              commute.getId(), commute.getWorkerId(), cmd.type(), cmd.description(), clock.instant());
      return Result.success(incidentRepository.save(incident));
    } catch (RuleViolation ex) {
      return Result.failure(new ApplicationError.BusinessRuleError(ex.getMessage()));
    }
  }

  @Override
  public Result<SafetyIncident, ApplicationError> handle(ResolveSafetyIncidentCommand cmd) {
    var incidentOpt = incidentRepository.findById(cmd.incidentId());
    if (incidentOpt.isEmpty()) {
      return Result.failure(
          new ApplicationError.NotFoundError("commuting.incident.not_found", "Incident not found"));
    }
    var incident = incidentOpt.get();
    boolean authorized =
        cmd.privileged()
            || incident.getWorkerId().equals(cmd.actorId())
            || profileDirectory.isActiveTrustLink(incident.getWorkerId(), cmd.actorId());
    if (!authorized) {
      return Result.failure(
          new ApplicationError.ForbiddenError("Not authorized to resolve this incident"));
    }
    try {
      incident.resolve();
      return Result.success(incidentRepository.save(incident));
    } catch (RuleViolation ex) {
      return Result.failure(new ApplicationError.BusinessRuleError(ex.getMessage()));
    }
  }

  @Override
  public int evaluateOverdueCommutes() {
    Instant now = clock.instant();
    int updated = 0;
    for (var commute : commuteRepository.findAll()) {
      if (("ACTIVE".equals(commute.getStatus()) || "DELAYED".equals(commute.getStatus()))
          && commute.isOverdueAt(now)) {
        commute.markOverdue(now);
        commuteRepository.save(commute);
        eventHandler.notifyOverdue(
            commute.getId(), commute.getWorkerId(), commute.getDestination(), now);
        updated++;
      }
    }
    return updated;
  }
}
