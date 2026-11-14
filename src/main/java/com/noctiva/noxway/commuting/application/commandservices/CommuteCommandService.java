package com.noctiva.noxway.commuting.application.commandservices;

import com.noctiva.noxway.commuting.application.commands.CommuteCommands.*;
import com.noctiva.noxway.commuting.domain.model.aggregates.Commute;
import com.noctiva.noxway.commuting.domain.model.aggregates.SafetyIncident;
import com.noctiva.noxway.commuting.domain.model.aggregates.TelemetryPing;
import com.noctiva.noxway.shared.application.result.ApplicationError;
import com.noctiva.noxway.shared.application.result.Result;

public interface CommuteCommandService {

  Result<Commute, ApplicationError> handle(StartCommuteCommand cmd);

  Result<TelemetryPing, ApplicationError> handle(RecordTelemetryPingCommand cmd);

  Result<Commute, ApplicationError> handle(DelayCommuteCommand cmd);

  Result<Commute, ApplicationError> handle(ArriveCommuteCommand cmd);

  Result<SafetyIncident, ApplicationError> handle(ReportSafetyIncidentCommand cmd);

  Result<SafetyIncident, ApplicationError> handle(ResolveSafetyIncidentCommand cmd);

  int evaluateOverdueCommutes();
}
