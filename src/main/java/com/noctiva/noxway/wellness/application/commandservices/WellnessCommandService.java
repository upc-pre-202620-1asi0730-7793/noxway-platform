package com.noctiva.noxway.wellness.application.commandservices;

import com.noctiva.noxway.shared.application.result.ApplicationError;
import com.noctiva.noxway.shared.application.result.Result;
import com.noctiva.noxway.wellness.application.commands.WellnessCommands.*;
import com.noctiva.noxway.wellness.domain.model.aggregates.RestSession;
import com.noctiva.noxway.wellness.domain.model.aggregates.SleepGoal;

public interface WellnessCommandService {

  Result<RestSession, ApplicationError> handle(LogRestSessionCommand command);

  Result<Void, ApplicationError> handle(DeleteRestSessionCommand command);

  Result<SleepGoal, ApplicationError> handle(UpdateSleepGoalCommand command);
}
