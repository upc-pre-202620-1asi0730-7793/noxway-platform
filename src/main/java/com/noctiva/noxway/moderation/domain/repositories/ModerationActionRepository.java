package com.noctiva.noxway.moderation.domain.repositories;

import com.noctiva.noxway.moderation.domain.model.aggregates.ModerationAction;
import com.noctiva.noxway.shared.domain.DomainRepository;
import java.util.UUID;

public interface ModerationActionRepository extends DomainRepository<ModerationAction> {
  java.util.List<ModerationAction> findByReportId(UUID value);

  java.util.List<ModerationAction> findByModeratorId(UUID value);
}
