package com.noctiva.noxway.moderation.domain.model.aggregates;

import static com.noctiva.noxway.shared.domain.Rules.require;

import com.noctiva.noxway.moderation.domain.model.events.ReportDecidedEvent;
import com.noctiva.noxway.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class CommunityReport extends AbstractDomainAggregateRoot<CommunityReport> {

  private final UUID id;
  private final UUID reportedBy;
  private final String targetType;
  private final UUID targetId;
  private final String reason;
  private String status;
  private final Instant createdAt;
  private final long version;

  private CommunityReport(
      UUID id,
      UUID reportedBy,
      String targetType,
      UUID targetId,
      String reason,
      String status,
      Instant createdAt,
      long version) {
    this.id = id;
    this.reportedBy = reportedBy;
    this.targetType = targetType;
    this.targetId = targetId;
    this.reason = reason;
    this.status = status;
    this.createdAt = createdAt;
    this.version = version;
  }

  public static CommunityReport create(
      UUID reportedBy, String targetType, UUID targetId, String reason, String status) {
    Objects.requireNonNull(reportedBy);
    Objects.requireNonNull(targetId);
    require("SERVICE".equals(targetType) || "RISK".equals(targetType), "Unsupported target type");
    require(reason != null && !reason.isBlank(), "Moderation reason is required");
    return new CommunityReport(
        UUID.randomUUID(),
        reportedBy,
        targetType,
        targetId,
        reason.trim(),
        status,
        Instant.now(),
        0L);
  }

  public static CommunityReport reconstitute(
      UUID id,
      UUID reportedBy,
      String targetType,
      UUID targetId,
      String reason,
      String status,
      Instant createdAt,
      long version) {
    return new CommunityReport(id, reportedBy, targetType, targetId, reason, status, createdAt, version);
  }

  public void decide(boolean approved, Instant decidedAt) {
    require("PENDING".equals(status), "Report has already been decided");
    this.status = approved ? "APPROVED" : "REJECTED";
    registerEvent(new ReportDecidedEvent(id, targetType, targetId, approved, decidedAt));
  }

  public UUID getId() {
    return id;
  }

  public UUID getReportedBy() {
    return reportedBy;
  }

  public String getTargetType() {
    return targetType;
  }

  public UUID getTargetId() {
    return targetId;
  }

  public String getReason() {
    return reason;
  }

  public String getStatus() {
    return status;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public long getVersion() {
    return version;
  }
}
