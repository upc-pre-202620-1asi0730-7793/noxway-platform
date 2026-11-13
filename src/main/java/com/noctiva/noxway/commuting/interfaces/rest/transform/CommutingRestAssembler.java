package com.noctiva.noxway.commuting.interfaces.rest.transform;

import com.noctiva.noxway.commuting.application.commands.CommuteCommands.*;
import com.noctiva.noxway.commuting.application.queryservices.CommuteQueryService.*;
import com.noctiva.noxway.commuting.domain.model.aggregates.*;
import com.noctiva.noxway.commuting.interfaces.rest.resources.*;
import com.noctiva.noxway.shared.interfaces.rest.resources.PagedResource;
import java.util.List;
import java.util.UUID;

public final class CommutingRestAssembler {
  private CommutingRestAssembler() {}

  public static StartCommuteCommand toCommand(UUID workerId, StartCommuteResource r) {
    return new StartCommuteCommand(
        workerId,
        r.origin(),
        r.destination(),
        r.transport(),
        r.startTime(),
        r.estimatedArrivalTime(),
        r.toleranceMinutes());
  }

  public static RecordTelemetryPingCommand toCommand(
      UUID commuteId, UUID actorId, RecordTelemetryPingResource r) {
    return new RecordTelemetryPingCommand(
        commuteId, actorId, r.latitude(), r.longitude(), r.speed(), r.recordedAt());
  }

  public static DelayCommuteCommand toCommand(UUID commuteId, UUID actorId, DelayCommuteResource r) {
    return new DelayCommuteCommand(commuteId, actorId, r.minutes());
  }

  public static ReportSafetyIncidentCommand toCommand(
      UUID commuteId, UUID actorId, ReportSafetyIncidentResource r) {
    return new ReportSafetyIncidentCommand(commuteId, actorId, r.type(), r.description());
  }

  public static CommuteResource toResource(Commute c) {
    if (c == null) {
      return null;
    }
    return new CommuteResource(
        c.getId(),
        c.getWorkerId(),
        c.getOrigin(),
        c.getDestination(),
        c.getTransport(),
        c.getStartTime(),
        c.getEstimatedArrivalTime(),
        c.getToleranceMinutes(),
        c.getStatus(),
        c.getArrivalTime(),
        c.getDelayUntil(),
        c.getCreatedAt());
  }

  public static PagedResource<CommuteResource> toPagedCommutes(PagedResource<Commute> page) {
    return new PagedResource<>(
        page.items().stream().map(CommutingRestAssembler::toResource).toList(),
        page.page(),
        page.size(),
        page.total());
  }

  public static TelemetryPingResource toResource(TelemetryPing p) {
    if (p == null) {
      return null;
    }
    return new TelemetryPingResource(
        p.getId(), p.getCommuteId(), p.getLatitude(), p.getLongitude(), p.getSpeed(), p.getRecordedAt());
  }

  public static List<TelemetryPingResource> toTelemetryResources(List<TelemetryPing> list) {
    return list.stream().map(CommutingRestAssembler::toResource).toList();
  }

  public static SafetyIncidentResource toResource(SafetyIncident i) {
    return new SafetyIncidentResource(
        i.getId(),
        i.getCommuteId(),
        i.getWorkerId(),
        i.getType(),
        i.getDescription(),
        i.getStatus(),
        i.getReportedAt(),
        i.getCreatedAt());
  }

  public static List<SafetyIncidentResource> toIncidentResources(List<SafetyIncident> list) {
    return list.stream().map(CommutingRestAssembler::toResource).toList();
  }

  public static AlertResource toResource(Alert a) {
    return new AlertResource(
        a.getId(),
        a.getCommuteId(),
        a.getIncidentId(),
        null,
        "",
        a.getContactId(),
        a.getChannel(),
        a.getStatus(),
        a.getMessage(),
        a.getDeliveredAt(),
        a.getCreatedAt());
  }

  public static List<AlertResource> toAlertDeliveryResources(List<Alert> list) {
    return list.stream().map(CommutingRestAssembler::toResource).toList();
  }

  public static PagedResource<AlertResource> toPagedAlerts(PagedResource<AlertView> page) {
    return new PagedResource<>(
        page.items().stream()
            .map(
                v ->
                    new AlertResource(
                        v.id(),
                        v.commuteId(),
                        v.incidentId(),
                        v.workerId(),
                        v.workerName(),
                        v.contactId(),
                        v.channel(),
                        v.status(),
                        v.message(),
                        v.deliveredAt(),
                        v.createdAt()))
            .toList(),
        page.page(),
        page.size(),
        page.total());
  }

  public static List<CompanionLiveResource> toCompanionLiveResources(
      List<CompanionWorkerLiveView> views) {
    return views.stream()
        .map(
            v ->
                new CompanionLiveResource(
                    v.workerId(),
                    v.workerName(),
                    v.workerPhone(),
                    v.shiftType(),
                    v.shiftStart(),
                    v.shiftEnd(),
                    toResource(v.activeCommute()),
                    toResource(v.latestPing()),
                    toIncidentResources(v.openIncidents()),
                    v.recentAlertCount()))
        .toList();
  }
}
