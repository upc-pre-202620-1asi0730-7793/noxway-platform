package com.noctiva.noxway.moderation.application.queryservices;

import com.noctiva.noxway.moderation.application.queries.ModerationQueries.*;
import com.noctiva.noxway.moderation.domain.model.aggregates.ModerationAction;
import com.noctiva.noxway.shared.application.result.ApplicationError;
import com.noctiva.noxway.shared.application.result.Result;
import com.noctiva.noxway.shared.interfaces.rest.resources.PagedResource;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface ModerationQueryService {

  Result<PagedResource<CommunityReportView>, ApplicationError> handle(GetModerationQueueQuery query);

  Result<List<ModerationActionView>, ApplicationError> handle(GetModerationAuditQuery query);

  record CommunityReportView(
      UUID id,
      UUID reportedBy,
      String reporterName,
      String targetType,
      UUID targetId,
      String targetTitle,
      String targetStatus,
      String reason,
      String status,
      Instant createdAt) {}

  record ModerationActionView(
      UUID id,
      UUID reportId,
      UUID moderatorId,
      String moderatorName,
      String decision,
      String notes,
      Instant createdAt) {}
}
