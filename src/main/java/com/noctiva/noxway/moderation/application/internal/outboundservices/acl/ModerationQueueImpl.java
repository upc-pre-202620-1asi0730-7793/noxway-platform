package com.noctiva.noxway.moderation.application.internal.outboundservices.acl;

import com.noctiva.noxway.moderation.domain.model.aggregates.CommunityReport;
import com.noctiva.noxway.moderation.domain.repositories.CommunityReportRepository;
import com.noctiva.noxway.moderation.interfaces.acl.ModerationQueue;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ModerationQueueImpl implements ModerationQueue {

  private final CommunityReportRepository reports;

  public ModerationQueueImpl(CommunityReportRepository reports) {
    this.reports = reports;
  }

  @Override
  public void submit(UUID reporter, String type, UUID target, String reason) {
    if (reports.findByTargetId(target).stream()
        .noneMatch(r -> "PENDING".equals(r.getStatus()))) {
      reports.save(CommunityReport.create(reporter, type, target, reason, "PENDING"));
    }
  }
}
