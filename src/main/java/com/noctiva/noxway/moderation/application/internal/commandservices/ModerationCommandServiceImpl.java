package com.noctiva.noxway.moderation.application.internal.commandservices;

import com.noctiva.noxway.community.interfaces.acl.CommunityModeration;
import com.noctiva.noxway.moderation.application.commands.ModerationCommands.*;
import com.noctiva.noxway.moderation.application.commandservices.ModerationCommandService;
import com.noctiva.noxway.moderation.domain.model.aggregates.CommunityReport;
import com.noctiva.noxway.moderation.domain.model.aggregates.ModerationAction;
import com.noctiva.noxway.moderation.domain.repositories.CommunityReportRepository;
import com.noctiva.noxway.moderation.domain.repositories.ModerationActionRepository;
import com.noctiva.noxway.shared.application.result.ApplicationError;
import com.noctiva.noxway.shared.application.result.Result;
import com.noctiva.noxway.shared.domain.RuleViolation;
import java.time.Clock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ModerationCommandServiceImpl implements ModerationCommandService {

  private final CommunityReportRepository reportRepository;
  private final ModerationActionRepository actionRepository;
  private final CommunityModeration communityModeration;
  private final Clock clock;

  public ModerationCommandServiceImpl(
      CommunityReportRepository reportRepository,
      ModerationActionRepository actionRepository,
      CommunityModeration communityModeration,
      Clock clock) {
    this.reportRepository = reportRepository;
    this.actionRepository = actionRepository;
    this.communityModeration = communityModeration;
    this.clock = clock;
  }

  @Override
  public Result<CommunityReport, ApplicationError> handle(SubmitCommunityReportCommand cmd) {
    if (!communityModeration.exists(cmd.targetType(), cmd.targetId())) {
      return Result.failure(
          new ApplicationError.NotFoundError(
              "moderation.target.not_found", "Moderation target does not exist"));
    }
    boolean alreadyPending =
        reportRepository.findByTargetId(cmd.targetId()).stream()
            .anyMatch(r -> "PENDING".equals(r.getStatus()));
    if (alreadyPending) {
      return Result.failure(
          new ApplicationError.ConflictError(
              "moderation.report.already_pending",
              "Target already has a pending moderation report"));
    }
    try {
      var report =
          CommunityReport.create(
              cmd.reportedBy(), cmd.targetType(), cmd.targetId(), cmd.reason(), "PENDING");
      return Result.success(reportRepository.save(report));
    } catch (RuleViolation ex) {
      return Result.failure(new ApplicationError.BusinessRuleError(ex.getMessage()));
    }
  }

  @Override
  public Result<CommunityReport, ApplicationError> handle(DecideReportCommand cmd) {
    var reportOpt = reportRepository.findById(cmd.reportId());
    if (reportOpt.isEmpty()) {
      return Result.failure(
          new ApplicationError.NotFoundError(
              "moderation.report.not_found", "Community report not found"));
    }
    var report = reportOpt.get();
    if (!"PENDING".equals(report.getStatus())) {
      return Result.failure(
          new ApplicationError.BusinessRuleError(
              "moderation.report.already_decided", "Community report has already been decided"));
    }
    boolean approved = "APPROVE".equals(cmd.decision());
    try {
      communityModeration.decide(report.getTargetType(), report.getTargetId(), approved);
      actionRepository.save(
          ModerationAction.create(report.getId(), cmd.moderatorId(), cmd.decision(), cmd.notes()));
      report.decide(approved, clock.instant());
      return Result.success(reportRepository.save(report));
    } catch (RuleViolation ex) {
      return Result.failure(new ApplicationError.BusinessRuleError(ex.getMessage()));
    }
  }
}
