package com.noctiva.noxway.commuting.application.internal.queryservices;

import com.noctiva.noxway.commuting.application.queries.CommuteQueries.*;
import com.noctiva.noxway.commuting.application.queryservices.CommuteQueryService;
import com.noctiva.noxway.commuting.domain.model.aggregates.*;
import com.noctiva.noxway.commuting.domain.repositories.*;
import com.noctiva.noxway.profiles.interfaces.acl.ProfileDirectoryFacade;
import com.noctiva.noxway.profiles.interfaces.acl.ProfileDirectoryFacade.AccountSummary;
import com.noctiva.noxway.shared.application.result.ApplicationError;
import com.noctiva.noxway.shared.application.result.Result;
import com.noctiva.noxway.shared.interfaces.rest.Pages;
import com.noctiva.noxway.shared.interfaces.rest.resources.PagedResource;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class CommuteQueryServiceImpl implements CommuteQueryService {

  private final CommuteRepository commuteRepository;
  private final TelemetryPingRepository telemetryRepository;
  private final SafetyIncidentRepository incidentRepository;
  private final AlertRepository alertRepository;
  private final ProfileDirectoryFacade profileDirectory;

  public CommuteQueryServiceImpl(
      CommuteRepository commuteRepository,
      TelemetryPingRepository telemetryRepository,
      SafetyIncidentRepository incidentRepository,
      AlertRepository alertRepository,
      ProfileDirectoryFacade profileDirectory) {
    this.commuteRepository = commuteRepository;
    this.telemetryRepository = telemetryRepository;
    this.incidentRepository = incidentRepository;
    this.alertRepository = alertRepository;
    this.profileDirectory = profileDirectory;
  }

  @Override
  public Result<PagedResource<Commute>, ApplicationError> handle(GetMyCommutesQuery query) {
    var all =
        commuteRepository.findByWorkerId(query.workerId()).stream()
            .sorted(Comparator.comparing(Commute::getStartTime).reversed())
            .toList();
    int page = Math.max(0, query.page());
    int size = Pages.clamp(query.size());
    int from = Math.min(page * size, all.size());
    int to = Math.min(from + size, all.size());
    return Result.success(new PagedResource<>(all.subList(from, to), page, size, all.size()));
  }

  @Override
  public Result<Commute, ApplicationError> handle(GetCommuteByIdQuery query) {
    var commuteOpt = commuteRepository.findById(query.commuteId());
    if (commuteOpt.isEmpty()) {
      return Result.failure(
          new ApplicationError.NotFoundError("commuting.not_found", "Commute not found"));
    }
    var commute = commuteOpt.get();
    if (!canAccessCommute(commute, query.actorId(), query.privileged())) {
      return Result.failure(
          new ApplicationError.ForbiddenError("Not authorized to view this commute"));
    }
    return Result.success(commute);
  }

  @Override
  public Result<List<TelemetryPing>, ApplicationError> handle(GetTelemetryPingsQuery query) {
    var commuteOpt = commuteRepository.findById(query.commuteId());
    if (commuteOpt.isEmpty()) {
      return Result.failure(
          new ApplicationError.NotFoundError("commuting.not_found", "Commute not found"));
    }
    if (!canAccessCommute(commuteOpt.get(), query.actorId(), query.privileged())) {
      return Result.failure(
          new ApplicationError.ForbiddenError("Not authorized to view commute telemetry"));
    }
    var pings =
        telemetryRepository.findByCommuteId(query.commuteId()).stream()
            .sorted(Comparator.comparing(TelemetryPing::getRecordedAt).reversed())
            .toList();
    return Result.success(pings);
  }

  @Override
  public Result<List<SafetyIncident>, ApplicationError> handle(GetCommuteIncidentsQuery query) {
    var commuteOpt = commuteRepository.findById(query.commuteId());
    if (commuteOpt.isEmpty()) {
      return Result.failure(
          new ApplicationError.NotFoundError("commuting.not_found", "Commute not found"));
    }
    if (!canAccessCommute(commuteOpt.get(), query.actorId(), query.privileged())) {
      return Result.failure(
          new ApplicationError.ForbiddenError("Not authorized to view commute incidents"));
    }
    var incidents =
        incidentRepository.findByCommuteId(query.commuteId()).stream()
            .sorted(Comparator.comparing(SafetyIncident::getReportedAt).reversed())
            .toList();
    return Result.success(incidents);
  }

  @Override
  public Result<List<CompanionWorkerLiveView>, ApplicationError> handle(
      GetCompanionLiveQuery query) {
    var workerIds = profileDirectory.activeWorkerIdsForContact(query.contactId());
    var myAlerts = alertRepository.findByContactId(query.contactId());
    var views =
        workerIds.stream()
            .flatMap(id -> profileDirectory.findById(id).stream())
            .map(
                worker -> {
                  var workerCommutes =
                      commuteRepository.findByWorkerId(worker.id()).stream()
                          .sorted(Comparator.comparing(Commute::getStartTime).reversed())
                          .toList();
                  var ongoing =
                      workerCommutes.stream().filter(Commute::isOngoing).findFirst().orElse(null);
                  var activeOrLast =
                      ongoing != null
                          ? ongoing
                          : (workerCommutes.isEmpty() ? null : workerCommutes.get(0));
                  TelemetryPing latestPing =
                      activeOrLast == null
                          ? null
                          : telemetryRepository.findByCommuteId(activeOrLast.getId()).stream()
                              .max(Comparator.comparing(TelemetryPing::getRecordedAt))
                              .orElse(null);
                  List<SafetyIncident> openIncidents =
                      incidentRepository.findByWorkerId(worker.id()).stream()
                          .filter(i -> "OPEN".equals(i.getStatus()))
                          .sorted(Comparator.comparing(SafetyIncident::getReportedAt).reversed())
                          .toList();
                  int recentAlerts =
                      (int)
                          myAlerts.stream()
                              .filter(
                                  a ->
                                      workerCommutes.stream()
                                          .anyMatch(c -> c.getId().equals(a.getCommuteId())))
                              .count();
                  return new CompanionWorkerLiveView(
                      worker.id(),
                      worker.fullName(),
                      worker.phone(),
                      worker.shiftType(),
                      worker.shiftStart(),
                      worker.shiftEnd(),
                      activeOrLast,
                      latestPing,
                      openIncidents,
                      recentAlerts);
                })
            .toList();
    return Result.success(views);
  }

  @Override
  public Result<PagedResource<AlertView>, ApplicationError> handle(GetCompanionAlertsQuery query) {
    Map<UUID, Commute> commuteMap =
        commuteRepository.findAll().stream().collect(Collectors.toMap(Commute::getId, c -> c));
    var alerts =
        alertRepository.findByContactId(query.contactId()).stream()
            .sorted(Comparator.comparing(Alert::getCreatedAt).reversed())
            .map(
                a -> {
                  var c = commuteMap.get(a.getCommuteId());
                  UUID workerId = c != null ? c.getWorkerId() : null;
                  String workerName =
                      workerId == null
                          ? ""
                          : profileDirectory
                              .findById(workerId)
                              .map(AccountSummary::fullName)
                              .orElse("");
                  return new AlertView(
                      a.getId(),
                      a.getCommuteId(),
                      a.getIncidentId(),
                      workerId,
                      workerName,
                      a.getContactId(),
                      a.getChannel(),
                      a.getStatus(),
                      a.getMessage(),
                      a.getDeliveredAt(),
                      a.getCreatedAt());
                })
            .toList();
    int page = Math.max(0, query.page());
    int size = Pages.clamp(query.size());
    int from = Math.min(page * size, alerts.size());
    int to = Math.min(from + size, alerts.size());
    return Result.success(new PagedResource<>(alerts.subList(from, to), page, size, alerts.size()));
  }

  private boolean canAccessCommute(Commute commute, UUID actorId, boolean privileged) {
    return privileged
        || commute.getWorkerId().equals(actorId)
        || profileDirectory.isActiveTrustLink(commute.getWorkerId(), actorId);
  }
}
