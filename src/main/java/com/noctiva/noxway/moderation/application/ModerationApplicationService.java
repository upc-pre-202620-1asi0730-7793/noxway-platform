package com.noctiva.noxway.moderation.application;

import static com.noctiva.noxway.shared.domain.Rules.*;

import com.noctiva.noxway.community.interfaces.acl.CommunityModeration;
import com.noctiva.noxway.moderation.domain.model.aggregates.*;
import com.noctiva.noxway.moderation.domain.repositories.*;
import com.noctiva.noxway.shared.infrastructure.security.Actor;
import java.util.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class ModerationApplicationService {
  private final CommunityReportRepository reports;
  private final ModerationActionRepository actions;
  private final CommunityModeration targets;

  public ModerationApplicationService(
      CommunityReportRepository r, ModerationActionRepository a, CommunityModeration t) {
    reports = r;
    actions = a;
    targets = t;
  }

  public List<CommunityReport> queue() {
    Actor.current().moderator();
    return reports.findByStatus("PENDING");
  }

  public CommunityReport report(String type, UUID target, String reason) {
    require(targets.exists(type, target), "Target does not exist");
    require(
        reports.findByTargetId(target).stream().noneMatch(r -> r.status().equals("PENDING")),
        "Target already has pending moderation");
    return reports.save(
        CommunityReport.create(Actor.current().id(), type, target, reason, "PENDING"));
  }

  public CommunityReport decide(UUID id, String decision, String notes) {
    Actor.current().moderator();
    var r =
        reports
            .findById(id)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Report not found"));
    require(r.status().equals("PENDING"), "Report already decided");
    targets.decide(r.targetType(), r.targetId(), decision.equals("APPROVE"));
    actions.save(ModerationAction.create(id, Actor.current().id(), decision, notes));
    return reports.save(r.withStatus(decision.equals("APPROVE") ? "APPROVED" : "REJECTED"));
  }

  public List<ModerationAction> audit(UUID id) {
    Actor.current().moderator();
    return actions.findByReportId(id);
  }
}
