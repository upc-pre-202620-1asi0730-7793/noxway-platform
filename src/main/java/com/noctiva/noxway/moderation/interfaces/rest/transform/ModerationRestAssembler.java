package com.noctiva.noxway.moderation.interfaces.rest.transform;

import com.noctiva.noxway.moderation.application.commands.ModerationCommands.*;
import com.noctiva.noxway.moderation.application.queryservices.ModerationQueryService.*;
import com.noctiva.noxway.moderation.domain.model.aggregates.CommunityReport;
import com.noctiva.noxway.moderation.interfaces.rest.resources.*;
import com.noctiva.noxway.shared.interfaces.rest.resources.PagedResource;
import java.util.List;
import java.util.UUID;

public final class ModerationRestAssembler {
  private ModerationRestAssembler() {}

  public static SubmitCommunityReportCommand toCommand(UUID reporterId, SubmitReportResource r) {
    return new SubmitCommunityReportCommand(reporterId, r.targetType(), r.targetId(), r.reason());
  }

  public static DecideReportCommand toCommand(
      UUID reportId, UUID moderatorId, ModerationDecisionResource r) {
    return new DecideReportCommand(reportId, moderatorId, r.decision(), r.notes());
  }

  public static CommunityReportResource toResource(CommunityReport r) {
    return new CommunityReportResource(
        r.getId(),
        r.getReportedBy(),
        "",
        r.getTargetType(),
        r.getTargetId(),
        r.getTargetType(),
        r.getStatus(),
        r.getReason(),
        r.getStatus(),
        r.getCreatedAt());
  }

  public static CommunityReportResource toResource(CommunityReportView v) {
    return new CommunityReportResource(
        v.id(),
        v.reportedBy(),
        v.reporterName(),
        v.targetType(),
        v.targetId(),
        v.targetTitle(),
        v.targetStatus(),
        v.reason(),
        v.status(),
        v.createdAt());
  }

  public static PagedResource<CommunityReportResource> toPagedResources(
      PagedResource<CommunityReportView> page) {
    return new PagedResource<>(
        page.items().stream().map(ModerationRestAssembler::toResource).toList(),
        page.page(),
        page.size(),
        page.total());
  }

  public static List<ModerationActionResource> toActionResources(List<ModerationActionView> list) {
    return list.stream()
        .map(
            a ->
                new ModerationActionResource(
                    a.id(),
                    a.reportId(),
                    a.moderatorId(),
                    a.moderatorName(),
                    a.decision(),
                    a.notes(),
                    a.createdAt()))
        .toList();
  }
}
