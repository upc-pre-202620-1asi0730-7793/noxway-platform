package com.noctiva.noxway.moderation.domain.model.aggregates;

import static com.noctiva.noxway.shared.domain.Rules.require;

import com.noctiva.noxway.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class ModerationAction extends AbstractDomainAggregateRoot<ModerationAction> {

  private final UUID id;
  private final UUID reportId;
  private final UUID moderatorId;
  private final String decision;
  private final String notes;
  private final Instant createdAt;
  private final long version;

  private ModerationAction(
      UUID id,
      UUID reportId,
      UUID moderatorId,
      String decision,
      String notes,
      Instant createdAt,
      long version) {
    this.id = id;
    this.reportId = reportId;
    this.moderatorId = moderatorId;
    this.decision = decision;
    this.notes = notes;
    this.createdAt = createdAt;
    this.version = version;
  }

  public static ModerationAction create(
      UUID reportId, UUID moderatorId, String decision, String notes) {
    Objects.requireNonNull(reportId);
    Objects.requireNonNull(moderatorId);
    require("APPROVE".equals(decision) || "REJECT".equals(decision), "Invalid moderation decision");
    require(notes != null && !notes.isBlank(), "Moderation notes are required");
    return new ModerationAction(
        UUID.randomUUID(), reportId, moderatorId, decision, notes.trim(), Instant.now(), 0L);
  }

  public static ModerationAction reconstitute(
      UUID id,
      UUID reportId,
      UUID moderatorId,
      String decision,
      String notes,
      Instant createdAt,
      long version) {
    return new ModerationAction(id, reportId, moderatorId, decision, notes, createdAt, version);
  }

  public UUID getId() {
    return id;
  }

  public UUID getReportId() {
    return reportId;
  }

  public UUID getModeratorId() {
    return moderatorId;
  }

  public String getDecision() {
    return decision;
  }

  public String getNotes() {
    return notes;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public long getVersion() {
    return version;
  }
}
