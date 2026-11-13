package com.noctiva.noxway.moderation.application.internal.queryservices;

import com.noctiva.noxway.community.interfaces.acl.CommunityModeration;
import com.noctiva.noxway.community.interfaces.acl.CommunityModeration.TargetSummary;
import com.noctiva.noxway.moderation.application.queries.ModerationQueries.*;
import com.noctiva.noxway.moderation.application.queryservices.ModerationQueryService;
import com.noctiva.noxway.moderation.domain.model.aggregates.CommunityReport;
import com.noctiva.noxway.moderation.domain.model.aggregates.ModerationAction;
import com.noctiva.noxway.moderation.domain.repositories.CommunityReportRepository;
import com.noctiva.noxway.moderation.domain.repositories.ModerationActionRepository;
import com.noctiva.noxway.profiles.interfaces.acl.ProfileDirectoryFacade;
import com.noctiva.noxway.profiles.interfaces.acl.ProfileDirectoryFacade.AccountSummary;
import com.noctiva.noxway.shared.application.result.ApplicationError;
import com.noctiva.noxway.shared.application.result.Result;
import com.noctiva.noxway.shared.interfaces.rest.Pages;
import com.noctiva.noxway.shared.interfaces.rest.resources.PagedResource;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ModerationQueryServiceImpl implements ModerationQueryService {

  private final CommunityReportRepository reportRepository;
  private final ModerationActionRepository actionRepository;
  private final CommunityModeration communityModeration;
  private final ProfileDirectoryFacade profileDirectory;

  public ModerationQueryServiceImpl(
      CommunityReportRepository reportRepository,
      ModerationActionRepository actionRepository,
      CommunityModeration communityModeration,
      ProfileDirectoryFacade profileDirectory) {
    this.reportRepository = reportRepository;
    this.actionRepository = actionRepository;
    this.communityModeration = communityModeration;
    this.profileDirectory = profileDirectory;
  }

  @Override
  public Result<PagedResource<CommunityReportView>, ApplicationError> handle(
      GetModerationQueueQuery query) {
    var list =
        (query.status() != null && !query.status().isBlank())
            ? reportRepository.findByStatus(query.status().trim().toUpperCase())
            : reportRepository.findAll();

    var views =
        list.stream()
            .sorted(
                Comparator.comparing(
                        (CommunityReport r) -> "PENDING".equals(r.getStatus()) ? 0 : 1)
                    .thenComparing(CommunityReport::getCreatedAt, Comparator.reverseOrder()))
            .map(
                r -> {
                  String reporterName =
                      profileDirectory
                          .findById(r.getReportedBy())
                          .map(AccountSummary::fullName)
                          .orElse(r.getReportedBy().toString());
                  var summary =
                      communityModeration
                          .summarize(r.getTargetType(), r.getTargetId())
                          .orElse(
                              new TargetSummary(
                                  r.getTargetId(), r.getTargetType(), r.getTargetType(), r.getStatus()));
                  return new CommunityReportView(
                      r.getId(),
                      r.getReportedBy(),
                      reporterName,
                      r.getTargetType(),
                      r.getTargetId(),
                      summary.title(),
                      summary.status(),
                      r.getReason(),
                      r.getStatus(),
                      r.getCreatedAt());
                })
            .toList();

    int page = Math.max(0, query.page());
    int size = Pages.clamp(query.size());
    int from = Math.min(page * size, views.size());
    int to = Math.min(from + size, views.size());
    return Result.success(new PagedResource<>(views.subList(from, to), page, size, views.size()));
  }

  @Override
  public Result<List<ModerationActionView>, ApplicationError> handle(
      GetModerationAuditQuery query) {
    var actions =
        actionRepository.findByReportId(query.reportId()).stream()
            .sorted(Comparator.comparing(ModerationAction::getCreatedAt).reversed())
            .map(
                a -> {
                  String modName =
                      profileDirectory
                          .findById(a.getModeratorId())
                          .map(AccountSummary::fullName)
                          .orElse(a.getModeratorId().toString());
                  return new ModerationActionView(
                      a.getId(),
                      a.getReportId(),
                      a.getModeratorId(),
                      modName,
                      a.getDecision(),
                      a.getNotes(),
                      a.getCreatedAt());
                })
            .toList();
    return Result.success(actions);
  }
}
