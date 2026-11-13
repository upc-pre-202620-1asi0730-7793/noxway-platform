package com.noctiva.noxway.wellness.application.internal.commandservices;

import com.noctiva.noxway.shared.application.result.ApplicationError;
import com.noctiva.noxway.shared.application.result.Result;
import com.noctiva.noxway.shared.domain.RuleViolation;
import com.noctiva.noxway.wellness.application.commands.WellnessCommands.*;
import com.noctiva.noxway.wellness.application.commandservices.WellnessCommandService;
import com.noctiva.noxway.wellness.domain.model.aggregates.RestSession;
import com.noctiva.noxway.wellness.domain.model.aggregates.SleepGoal;
import com.noctiva.noxway.wellness.domain.repositories.RestSessionRepository;
import com.noctiva.noxway.wellness.domain.repositories.SleepGoalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class WellnessCommandServiceImpl implements WellnessCommandService {

  private final RestSessionRepository restRepository;
  private final SleepGoalRepository goalRepository;

  public WellnessCommandServiceImpl(
      RestSessionRepository restRepository, SleepGoalRepository goalRepository) {
    this.restRepository = restRepository;
    this.goalRepository = goalRepository;
  }

  @Override
  public Result<RestSession, ApplicationError> handle(LogRestSessionCommand cmd) {
    var existing = restRepository.findByWorkerId(cmd.workerId());
    boolean hasOverlap =
        existing.stream().anyMatch(s -> s.overlaps(cmd.startTime(), cmd.endTime()));
    if (hasOverlap) {
      return Result.failure(
          new ApplicationError.BusinessRuleError(
              "wellness.rest_session.overlap",
              "Rest session overlaps an existing recorded rest interval"));
    }
    try {
      var session =
          RestSession.create(
              cmd.workerId(), cmd.startTime(), cmd.endTime(), cmd.quality(), cmd.notes());
      return Result.success(restRepository.save(session));
    } catch (RuleViolation ex) {
      return Result.failure(new ApplicationError.BusinessRuleError(ex.getMessage()));
    }
  }

  @Override
  public Result<Void, ApplicationError> handle(DeleteRestSessionCommand cmd) {
    var found = restRepository.findById(cmd.sessionId());
    if (found.isEmpty()) {
      return Result.failure(
          new ApplicationError.NotFoundError(
              "wellness.rest_session.not_found", "Rest session not found"));
    }
    if (!found.get().getWorkerId().equals(cmd.actorId())) {
      return Result.failure(
          new ApplicationError.ForbiddenError("You can only delete your own rest sessions"));
    }
    restRepository.delete(cmd.sessionId());
    return Result.success(null);
  }

  @Override
  public Result<SleepGoal, ApplicationError> handle(UpdateSleepGoalCommand cmd) {
    try {
      var existing = goalRepository.findByWorkerId(cmd.workerId());
      SleepGoal goal;
      if (existing.isEmpty()) {
        goal = SleepGoal.create(cmd.workerId(), cmd.targetMinutes());
      } else {
        goal = existing.get(0);
        goal.updateTarget(cmd.targetMinutes());
      }
      return Result.success(goalRepository.save(goal));
    } catch (RuleViolation ex) {
      return Result.failure(new ApplicationError.BusinessRuleError(ex.getMessage()));
    }
  }
}
