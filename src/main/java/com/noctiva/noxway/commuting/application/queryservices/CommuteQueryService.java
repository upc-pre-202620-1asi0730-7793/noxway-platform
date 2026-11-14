package com.noctiva.noxway.commuting.application.queryservices;

import com.noctiva.noxway.commuting.application.queries.CommuteQueries.*;
import com.noctiva.noxway.commuting.domain.model.aggregates.*;
import com.noctiva.noxway.shared.application.result.ApplicationError;
import com.noctiva.noxway.shared.application.result.Result;
import com.noctiva.noxway.shared.interfaces.rest.resources.PagedResource;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface CommuteQueryService {

  Result<PagedResource<Commute>, ApplicationError> handle(GetMyCommutesQuery query);

  Result<Commute, ApplicationError> handle(GetCommuteByIdQuery query);

  Result<List<TelemetryPing>, ApplicationError> handle(GetTelemetryPingsQuery query);

  Result<List<SafetyIncident>, ApplicationError> handle(GetCommuteIncidentsQuery query);

  Result<List<CompanionWorkerLiveView>, ApplicationError> handle(GetCompanionLiveQuery query);

  Result<PagedResource<AlertView>, ApplicationError> handle(GetCompanionAlertsQuery query);

  record CompanionWorkerLiveView(
      UUID workerId,
      String workerName,
      String workerPhone,
      String shiftType,
      String shiftStart,
      String shiftEnd,
      Commute activeCommute,
      TelemetryPing latestPing,
      List<SafetyIncident> openIncidents,
      int recentAlertCount) {}

  record AlertView(
      UUID id,
      UUID commuteId,
      UUID incidentId,
      UUID workerId,
      String workerName,
      UUID contactId,
      String channel,
      String status,
      String message,
      Instant deliveredAt,
      Instant createdAt) {}
}
